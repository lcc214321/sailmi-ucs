/**
 * Copyright (c) 2018-2028.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sailmi.system.user.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.sailmi.system.entity.Result;
import com.sailmi.system.entity.UserEnterprise;
import com.sailmi.system.feign.IuserEnterRelationFeign;
import com.sailmi.system.user.entity.UcsAccountuser;
import com.sailmi.system.user.persist.AliceUserModel;
import com.sailmi.system.user.service.knowniot.IAliceUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.Charsets;
import com.sailmi.core.mp.support.Condition;
import com.sailmi.core.mp.support.Query;
import com.sailmi.core.secure.AuthUser;
import com.sailmi.core.secure.utils.SecureUtil;
import com.sailmi.core.tool.api.R;
import com.sailmi.core.tool.constant.AppConstant;
import com.sailmi.core.tool.utils.Func;
import com.sailmi.system.user.entity.User;
import com.sailmi.system.user.excel.UserExcel;
import com.sailmi.system.user.excel.UserImportListener;
import com.sailmi.system.user.service.IUserService;
import com.sailmi.system.user.vo.UserVO;
import com.sailmi.system.user.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@RequestMapping
@AllArgsConstructor
public class UserController {

	private IUserService userService;
	private IuserEnterRelationFeign iuserEnterRelationFeign;

	/******************************************************
	 *                  Public House 2nd
	 ******************************************************/
	@PostMapping("/alice/knowniot/createUser")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "创建员工，关联企业、部门", notes = "传入")
	public R createKnowniotUser(UcsAccountuser user) throws Exception {
		String s = userService.registerUserV2(user);
		if(s.equals("fail")){
			return R.fail("注册失败");
		} else if (s.equals("phone")) {
			return R.fail("此手机号码已被注册: " + user.getUserPhone());
		} else if(s.equals("success")){
			return R.success("注册成功");
		}else{
			return R.fail("验证码超时");
		}
	}

	@GetMapping("/alice/knowniot/getUser")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "获取用户信息", notes = "传入")
	public R getKnowniotUser(String tenantId, String account, String password) throws Exception {
		return R.data(userService.userInfoV2(tenantId,account, password));
	}

	@PostMapping("/alice/knowniot/editUser")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "修改用户信息", notes = "传入")
	public R editKnowniotUser(UcsAccountuser user) throws Exception {
		return R.data(userService.editKnowniotUser(user));
	}

	@PostMapping("/alice/knowniot/delUser")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "删除用户", notes = "传入")
	public R delKnowniotUser(Long userId, boolean isSoft) throws Exception {
		String s = userService.delKnowniotUser(userId, isSoft);
		if (s.equals("success")) {
			return R.success("删除成功");
		} else if (s.equals("notExists")) {
			return R.fail("用户不存在");
		} else {
			return R.fail("删除失败");
		}
	}

	/******************************************************
	 *                  Ucs
	 ******************************************************/
	/**
	 * 注册用户
	 * @param user
	 * @return
	 */
	@PostMapping("/register")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "注册用户", notes = "传入UcsAccountuser")
	public R registerUser( UcsAccountuser user) {
		String s = userService.registerUser(user);
		if(s.equals("fail")){
			return R.fail("验证码错误");
		}else if(s.equals("success")){
			return R.success("注册成功");
		}else{
			return R.fail("验证码超时");
		}
	}

	/**
	 * 发送短信验证码
	 * @param userPhone
	 * @return
	 */
	@PostMapping("/sendPhoneCode")
	@ApiOperationSupport(order = 1)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userPhone", value = "手机号",  dataType = "string"),
	})
	@ApiOperation(value = "发送验证码短信", notes = "传入userPhone")
	public R sendPhoneCode(@RequestParam String userPhone) {
		return R.status(userService.sendPhoneCode(userPhone));
	}

	/**
	 * <p>Description: 忘记密码中的验证码</p>
	 *
	 * @param userPhone:
	 * @param code:
	 * @return: com.sailmi.system.entity.Result
	 * @Author: syt
	 * @Date: 2020/11/12/0012 14:59
	 */
	@RequestMapping(value="checkCode",method=RequestMethod.POST)
	@ResponseBody
	public Result checkPhoneCode(String userPhone,String code) {

		return userService.checkPhoneCode(userPhone,code);

	}

	/**
	 * 验证手机号唯一
	 * @param userPhone
	 * @return
	 */
	@PostMapping("/uniquePhone")
	@ApiOperationSupport(order = 1)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userPhone", value = "手机号",  dataType = "string"),
	})
	@ApiOperation(value = "验证手机号唯一", notes = "传入userPhone")
	public R queryPhoneCode(@RequestParam String userPhone) {
		int count = userService.queryUnikePhone(userPhone);
		if(count>0){
			return R.fail("该手机号已经被注册");
		}else{
			return R.success("可以使用该手机号");
		}

	}

	/****
	 * @author GisonWin
	 * @Date 2019-12-24
	 * 验证手机号是否存在于系统中.  由于有台接口更改,该接口仅用于忘记密码内的校验
	 * @param userPhone
	 * @return
	 */
	@RequestMapping(value="uniquePhone1",method=RequestMethod.POST)
	@ResponseBody
	public Result queryUniquePhone(String userPhone) {

		return userService.queyrUniquePhone(userPhone);

	}

	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查看用户基础信息详情", notes = "传入id")
	@GetMapping("/detail")
	public R<UserVO> detail(User user) {
		User detail = userService.getOne(Condition.getQueryWrapper(user));
		return R.data(UserWrapper.build().entityVO(detail));
	}

	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查看用户基础信息详情", notes = "传入id")
	@PostMapping("/userdetail")
	public R<User> detail(HttpServletRequest request) {
		String id = request.getParameter("id");
		User detail=null;
		if(id!=null) {
			QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
			userQueryWrapper.eq("id", id);
			 detail = userService.getOne(userQueryWrapper);
		}
		if(detail!=null) {
			return R.data(detail);
		}else{
			return R.data(null);
		}
	}
	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order =2)
	@ApiOperation(value = "查看用户综合信息详情", notes = "传入id")
	@GetMapping("/info")
	public R<UserVO> info(AuthUser user) {
		System.out.println("AuthUser:"+user);
		User detail = userService.getById(user.getUserId());
		return R.data(UserWrapper.build().entityVO(detail));
	}

	/**
	 * 用户列表
	 */
	@GetMapping("/list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", value = "账号名", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "realName", value = "姓名", paramType = "query", dataType = "string")
	})
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "列表", notes = "传入account和realName")
	public R<IPage<UserVO>> list(@ApiIgnore @RequestParam Map<String, Object> user, Query query, AuthUser authUser) {
		//QueryWrapper<User> queryWrapper = Condition.getQueryWrapper(user, User.class);
		QueryWrapper<User> queryWrapper =new QueryWrapper<>();
		IPage<User>	pages=null;
		if(authUser!=null) {
			ArrayList<Long> userIds = new ArrayList<>();
			//ucs_enterprise_user表要用起来  yzh
				if (authUser.getEnterpriseId() != null) {
					String enterpriseId = authUser.getEnterpriseId();
					R<List<UserEnterprise>> listR = iuserEnterRelationFeign.detailInfo(enterpriseId);
					if(listR!=null && listR.getData()!=null && listR.getData().size()>0){
						listR.getData().stream().forEach(UserEnterprise->{
							userIds.add(UserEnterprise.getUserId());
						});
					}
				}
			if(userIds.size()>0){
				queryWrapper.in("id",userIds);
				pages = userService.page(Condition.getPage(query),  queryWrapper);
			}
		}
		if(pages!=null && pages.getTotal()>0) {
			return R.data(UserWrapper.build().pageVO(pages));
		}else{
			return R.data(null);
		}
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增或修改", notes = "传入User")
	public R submit(@Valid @RequestBody User user) {
		return R.status(userService.submit(user));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入User")
	public R update(@Valid @RequestBody User user) {
		return R.status(userService.updateById(user));
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "删除", notes = "传入要操作的用户ID列表")
	public R remove(@RequestParam String ids) {
		return R.status(userService.deleteLogic(Func.toLongList(ids)));
	}


	/**
	 * 设置菜单权限
	 *
	 * @param userIds
	 * @param roleIds
	 * @return
	 */
	@PostMapping("/grant")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "权限设置", notes = "传入roleId集合以及menuId集合")
	public R grant(@ApiParam(value = "userId集合", required = true) @RequestParam String userIds,
				   @ApiParam(value = "roleId集合", required = true) @RequestParam String roleIds) {
		boolean temp = userService.grant(userIds, roleIds);
		return R.status(temp);
	}

	@PostMapping("/reset-password")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "初始化密码", notes = "传入userId集合")
	public R resetPassword(@ApiParam(value = "userId集合", required = true) @RequestParam String userIds) {
		boolean temp = userService.resetPassword(userIds);
		return R.status(temp);
	}

	/**
	 * 修改密码
	 *
	 * @param oldPassword
	 * @param newPassword
	 * @param newPassword1
	 * @return
	 */
	@PostMapping("/update-password")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "修改密码", notes = "传入密码")
	public R updatePassword(AuthUser user, @ApiParam(value = "旧密码", required = true) @RequestParam String oldPassword,
							@ApiParam(value = "新密码", required = true) @RequestParam String newPassword,
							@ApiParam(value = "新密码", required = true) @RequestParam String newPassword1) {
		boolean temp = userService.updatePassword(user.getUserId(), oldPassword, newPassword, newPassword1);
		return R.status(temp);
	}

	/**
	 * 用户列表
	 *
	 * @param user
	 * @return
	 */
	@GetMapping("/user-list")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "用户列表", notes = "传入user")
	public R<List<User>> userList(User user) {
		List<User> list = userService.list(Condition.getQueryWrapper(user));
		return R.data(list);
	}


	/**
	 * 导入用户
	 */
	@PostMapping("import-user")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "导入用户", notes = "传入excel")
	public R importUser(MultipartFile file, Integer isCovered) {
		String filename = file.getOriginalFilename();
		if (StringUtils.isEmpty(filename)) {
			throw new RuntimeException("请上传文件!");
		}
		if ((!StringUtils.endsWithIgnoreCase(filename, ".xls") && !StringUtils.endsWithIgnoreCase(filename, ".xlsx"))) {
			throw new RuntimeException("请上传正确的excel文件!");
		}
		InputStream inputStream;
		try {
			UserImportListener importListener = new UserImportListener(userService);
			inputStream = new BufferedInputStream(file.getInputStream());
			ExcelReaderBuilder builder = EasyExcel.read(inputStream, UserExcel.class, importListener);
			builder.doReadAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return R.success("操作成功");
	}

	/**
	 * 导出用户
	 */
	@SneakyThrows
	@GetMapping("export-user")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "导出用户", notes = "传入user")
	public void exportUser(@ApiIgnore @RequestParam Map<String, Object> user, AuthUser authUser, HttpServletResponse response) {
		QueryWrapper<User> queryWrapper = Condition.getQueryWrapper(user, User.class);
		if (!SecureUtil.isAdministrator()){
			queryWrapper.lambda().eq(User::getDefaultEnterpriseId, authUser.getTenantId());
		}
		queryWrapper.lambda().eq(User::getIsDeleted, AppConstant.DB_NOT_DELETED);
		List<UserExcel> list = userService.exportUser(queryWrapper);
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding(Charsets.UTF_8.name());
		String fileName = URLEncoder.encode("用户数据导出", Charsets.UTF_8.name());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		EasyExcel.write(response.getOutputStream(), UserExcel.class).sheet("用户数据表").doWrite(list);
	}

	/**
	 * 导出模板
	 */
	@SneakyThrows
	@GetMapping("export-template")
	@ApiOperationSupport(order = 14)
	@ApiOperation(value = "导出模板")
	public void exportUser(HttpServletResponse response) {
		List<UserExcel> list = new ArrayList<>();
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding(Charsets.UTF_8.name());
		String fileName = URLEncoder.encode("用户数据模板", Charsets.UTF_8.name());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		EasyExcel.write(response.getOutputStream(), UserExcel.class).sheet("用户数据表").doWrite(list);
	}


	@RequestMapping(value="updatePass",method=RequestMethod.POST)
	@ResponseBody
	public Result updatePassword(String userPhone, String userEmail, String password) {

		return userService.updatePassword1(userPhone,userEmail,password);

	}

}
