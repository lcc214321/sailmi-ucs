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
package com.sailmi.system.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.google.common.base.Joiner;
import com.sailmi.system.entity.ResponseMessage;
import com.sailmi.system.entity.Result;
import com.sailmi.system.user.entity.UcsAccountuser;
import com.sailmi.system.user.excel.MD5Tools;
import com.sailmi.system.user.excel.PhoneCodeUtil;
import com.sailmi.system.user.vo.UserVO;
import lombok.AllArgsConstructor;
import com.sailmi.common.constant.CommonConstant;
import com.sailmi.core.log.exception.ServiceException;
import com.sailmi.core.mp.base.BaseServiceImpl;
import com.sailmi.core.tool.utils.*;
import com.sailmi.system.feign.ISysClient;
import com.sailmi.system.user.entity.User;
import com.sailmi.system.user.entity.UserInfo;
import com.sailmi.system.user.excel.UserExcel;
import com.sailmi.system.user.mapper.UserMapper;
import com.sailmi.system.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {

	private ISysClient sysClient;
	@Resource
	private PhoneCodeUtil PhoneCodeUtil;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Resource
	private  UserMapper userMapper;

	@Override
	public String registerUser(UcsAccountuser users) {
		String key = "phoneCode:register" + users.getUserPhone();
		Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
		String status = "";
		if (expire < 0) {
			status = "timeout";
		} else {
			String redisCode = stringRedisTemplate.opsForValue().get(key);
			if (redisCode != null) {
				if (redisCode.equals(users.getCode())) {//验证码验证成功
					User user = new User();
					if (users.getUserPhone() != null) {//手机号和账号
						user.setPhone(users.getUserPhone());
						user.setAccount(users.getUserPhone());
						String md5 = MD5Tools.MD5(users.getUserPhone());//安全码
						user.setCode(md5);
					}
					if (users.getPassword() != null) {
						user.setPassword(DigestUtil.encrypt(users.getPassword()));
					}
					user.setTenantId("000000");//这里hardcode了,应该获取当前系统的tenantid   yzh
					user.setDefaultEnterpriseId(Long.valueOf(0));//默认公司是0
					user.setLastLogin(String.valueOf(new Date().getTime()));//登陆时间
					user.setCreateTime(new Date());//创建时间
					user.setPhoneStatus("1");//用户手机号注册状态是1   syt
					int insert = baseMapper.insert(user);
					status = "success";
				} else {//验证码验证失败
					status = "fail";
				}

			}
		}
		return status;
	}

	/**
	 * 注册用户 不用短信认证
	 * @param user
	 * @return
	 */
	@Override
	public String registerUserV2(UcsAccountuser users) {
		User user = new User();
		if (!StringUtils.isEmpty(users.getUserPhone())) {//手机号和账号
			int count = queryUnikePhone(users.getUserPhone());
			if (count > 0) {
				return "phone";
			}
			user.setPhone(users.getUserPhone());
			user.setAccount(users.getUserPhone());
			String md5 = MD5Tools.MD5(users.getUserPhone());//安全码
			user.setCode(md5);
			user.setRealName(users.getRealName());
			user.setName(users.getUserPhone());
		}
		if (users.getPassword() != null) {
			user.setPassword(DigestUtil.encrypt(users.getPassword()));
		}
		user.setTenantId(users.getTenantId());
		user.setDefaultEnterpriseId(users.getLastEnterpriseId());//默认公司是0
		user.setLastLogin(String.valueOf(new Date().getTime()));//登陆时间
		user.setCreateTime(new Date());//创建时间
		int insert = baseMapper.insert(user);
		if (insert > 0) {
			return "success";
		}
		return "fail";
	}

	@Override
	public UserInfo userInfoV2(String tenantId,String account, String password) {
		UserInfo userInfo = new UserInfo();
		UserVO userVO = new UserVO();
		User user = baseMapper.getUser(tenantId, account, password);
		userVO = BeanUtil.copy(user, UserVO.class);//用户信息封装
		userInfo.setUser(userVO);
		return userInfo;
	}

	@Override
	public UserInfo editKnowniotUser(UcsAccountuser users) {
		User user = baseMapper.selectById(users.getId());
		if (!user.getPhone().equals(users.getUserPhone())) {
			int count = queryUnikePhone(users.getUserPhone());
			if (count > 0) {
				throw new ApiException("当前手机号码已存在!");
			}
		}
		user.setPhone(users.getUserPhone());
		user.setAccount(users.getUserPhone());
		String md5 = MD5Tools.MD5(users.getUserPhone());//安全码
		user.setCode(md5);
		user.setRealName(users.getRealName());
		user.setName(users.getUserPhone());
		if (users.getPassword() != null) {
			user.setPassword(DigestUtil.encrypt(users.getPassword()));
		}
		user.setUpdateTime(new Date());//创建时间

		int update = baseMapper.updateById(user);
		if (update > 0) {
			return userInfoV2(user.getTenantId(), user.getAccount(), user.getPassword());
		}

		return null;
	}

	@Override
	public String delKnowniotUser(Long userId, boolean isSoft) throws Exception {
		if (isSoft) {
			return "success";
		}

		Integer cnt = baseMapper.selectCount(Wrappers.<User>query().lambda().eq(User::getId, userId));
		if (cnt == 0) {
			return "notExists";
		}

		if (baseMapper.deleteById(userId) > 0) {
			return "success";
		}

		return "fail";
	}

			@Override
			public boolean submit(User user) {
				if (Func.isNotEmpty(user.getPassword())) {
					user.setPassword(DigestUtil.encrypt(user.getPassword()));
				}
				Integer cnt = baseMapper.selectCount(Wrappers.<User>query().lambda().eq(User::getDefaultEnterpriseId, user.getDefaultEnterpriseId()).eq(User::getAccount, user.getAccount()));
				if (cnt > 0) {
					throw new ApiException("当前用户已存在!");
				}
				return saveOrUpdate(user);
			}

			@Override
			public IPage<User> selectUserPage(IPage<User> page, User user) {
				return page.setRecords(baseMapper.selectUserPage(page, user));
			}

			@Override
			public UserInfo userInfo(Long userId) {
				UserInfo userInfo = new UserInfo();
				User user = baseMapper.selectById(userId);

				UserVO userVO = new UserVO();
				userVO = BeanUtil.copy(user, UserVO.class);
				userInfo.setUser(userVO);


				if (Func.isNotEmpty(user)) {
					List<String> roleAlias =new ArrayList<>();
					//TODO 查询用户角色信息
					//baseMapper.getRoleAlias(Func.toStrArray(user.getRoleId()));
					List<String> roles = baseMapper.queryUserRoles(user.getId().toString(), user.getDefaultEnterpriseId().toString());
					if(roles!=null && roles.size()>0) {
						userVO.setRoleId(roles.get(0));
						roleAlias = baseMapper.getRoleAlias(Func.toStrArray(roles.get(0)));
						userInfo.setRoles(roleAlias);
					}

				}
				return userInfo;
			}


	@Override
			public UserInfo userInfo(String tenantId,String account, String password) {
				UserInfo userInfo = new UserInfo();
				UserVO userVO = new UserVO();
				//User user = baseMapper.getUser(tenantId, account, password);
				User user = baseMapper.getConsoleUser(account,password);
				if(user!=null){
					userVO = BeanUtil.copy(user, UserVO.class);//用户信息封装
						List<String> roles = baseMapper.queryUserRoles(user.getId().toString(), user.getDefaultEnterpriseId().toString());
						if (roles != null && roles.size() > 0) {
							String roleIds = Joiner.on(",").join(roles);
							if (roleIds != null && roleIds != "") {
								List<String> roleAlias = baseMapper.getRoleAlias(Func.toStrArray(roleIds));
								if (roleAlias != null && roleAlias.size() > 0) {
									userInfo.setRoles(roleAlias);
								}
								// 用户角色信息
								userVO.setRoleId(roleIds);
							}
						}
					userInfo.setUser(userVO);
				}else{
					return null;
				}
				return userInfo;
			}

			@Override
			public boolean grant(String userIds, String roleIds) {
				User user = new User();
				//user.setRoleId(roleIds);
				return this.update(user, Wrappers.<User>update().lambda().in(User::getId, Func.toLongList(userIds)));
			}

			@Override
			public boolean resetPassword(String userIds) {
				User user = new User();
				user.setPassword(DigestUtil.encrypt(CommonConstant.DEFAULT_PASSWORD));
				user.setUpdateTime(DateUtil.now());
				return this.update(user, Wrappers.<User>update().lambda().in(User::getId, Func.toLongList(userIds)));
			}

			@Override
			public boolean updatePassword(Long userId, String oldPassword, String newPassword, String newPassword1) {
				User user = getById(userId);
				if (!newPassword.equals(newPassword1)) {
					throw new ServiceException("请输入正确的确认密码!");
				}
				if (!user.getPassword().equals(DigestUtil.encrypt(oldPassword))) {
					throw new ServiceException("原密码不正确!");
				}
				return this.update(Wrappers.<User>update().lambda().set(User::getPassword, DigestUtil.encrypt(newPassword)).eq(User::getId, userId));
			}

			@Override
			public List<String> getRoleName(String roleIds) {
				return baseMapper.getRoleName(Func.toStrArray(roleIds));
			}

			@Override
			public List<String> getDeptName(String deptIds) {
				return baseMapper.getDeptName(Func.toStrArray(deptIds));
			}

			@Override
			public void importUser(List<UserExcel> data) {
				data.forEach(userExcel -> {
					User user = Objects.requireNonNull(BeanUtil.copy(userExcel, User.class));
					// 设置部门ID
					//user.setDeptId(sysClient.getDeptIds(userExcel.getTenantId(), userExcel.getDeptName()));
					// 设置岗位ID
					//user.setPostId(sysClient.getPostIds(userExcel.getTenantId(), userExcel.getPostName()));
					// 设置角色ID
					//user.setRoleId(sysClient.getRoleIds(userExcel.getTenantId(), userExcel.getRoleName()));
					// 设置默认密码
					user.setPassword(CommonConstant.DEFAULT_PASSWORD);
					this.submit(user);
				});
			}

			@Override
			public List<UserExcel> exportUser(Wrapper<User> queryWrapper) {
				List<UserExcel> userList = baseMapper.exportUser(queryWrapper);
				userList.forEach(user -> {
					user.setRoleName(StringUtil.join(sysClient.getRoleNames(user.getRoleId())));
					user.setDeptName(StringUtil.join(sysClient.getDeptNames(user.getDeptId())));
					user.setPostName(StringUtil.join(sysClient.getPostNames(user.getPostId())));
				});
				return userList;
			}

			@Override
			public List<String> queryUserRoles(String id,String defaultEnterpriseId,String tenantId) {
				List<String>role=baseMapper.queryUserRoles(id,defaultEnterpriseId);
				return role;
			}

			@Override
			public Boolean sendPhoneCode(String userPhone) {
				boolean status=true;
				try {
					String sendCodeMsg = PhoneCodeUtil.sendCodeMsg(userPhone);
					if (sendCodeMsg.contains("Success")) {
					} else {
						status = false;
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				return status;
			}

	@Override
	public int queryUnikePhone(String userPhone) {
		Integer cnt = baseMapper.selectCount(Wrappers.<User>query().lambda().eq(User::getAccount, userPhone).eq(User::getPhone, userPhone));
		return cnt;
	}

	@Override
	public Long submitUser(User user) {
		int i = baseMapper.submitUserInfo(user);
		return user.getId();
	}

	@Override
	public int resetUserPassById(String id) {
		String pass="123456";
		return baseMapper.resetUserPass(id,DigestUtil.encrypt(pass));
	}

	@Override
	public Result updatePassword1(String userPhone, String userEmail, String password) {
//		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		String hashPass = passwordEncoder.encode(password);
		Result result = new Result();
		String userId="";
		if(userPhone!=null && userPhone.length()>0) {
			userId=baseMapper.queryUserIdByPhone(userPhone);
		}
		if(userEmail!=null && userEmail.length()>0){
			userId=baseMapper.queryUserIdByEmail(userEmail);
		}
		System.out.println(DigestUtil.encrypt(password));
		int status=baseMapper.updateUserPass(userId,DigestUtil.encrypt(password));
		if(status>0) {
			result.setCode(ResponseMessage.SUCCESS);
			result.setMsg("修改密码成功");
		}else {
			result.setCode(ResponseMessage.SUCCESS);
			result.setMsg("修改密码失败");
		}
		return result;
	}

	@Override
	public Result checkPhoneCode(String userPhone, String code) {
		Result result = new Result();
		String key = "phoneCode:check"+userPhone;
//		Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
		String redisCode = stringRedisTemplate.opsForValue().get(key);
		if (redisCode != null) {
			if (redisCode.equals(code)) {//验证码验证成功
				result.setCode(ResponseMessage.SUCCESS);
				result.setMsg("验证成功");
			}else {
				result.setCode(ResponseMessage.PARAERROE);
				result.setMsg("验证码失败");
			}
			return result;
		}
		result.setCode(ResponseMessage.PARAERROE);
		result.setMsg("验证码失败");
		return result;
//		if(expire<0) {
//			result.setCode(ResponseMessage.TIMEOUT);
//			result.setMsg("验证码超时");
//		}else {
//			String redisCode = stringRedisTemplate.opsForValue().get(key);
//			if(redisCode!=null) {
//				if(redisCode.equals(code)) {//验证码验证成功
//					result.setCode(ResponseMessage.SUCCESS);
//					result.setMsg("验证成功");
//				}else {
//					result.setCode(ResponseMessage.PARAERROE);
//					result.setMsg("验证码错误");
//				}
//			}
//		}

	}
	/***
	 * <p>Description: </p>
	 * 验证手机号是否存在于系统中.  由于有台接口更改,该接口仅用于忘记密码内的校验
	 * @return: com.sailmi.system.entity.Result
	 * @Author: syt
	 * @Date: 2020/11/12/0012 16:20
	 */
	@Override
	public Result queyrUniquePhone(String userPhone) {
		Result response = new Result();
		int queryPhoneNum = userMapper.queryPhoneNum(userPhone);
		if(queryPhoneNum>0) {
			response.setCode("1");
			response.setMsg("该手机号已经存在");
		}else {
			response.setCode("0");
			response.setMsg("该手机号不存存在");
		}
		return response;
	}
}
