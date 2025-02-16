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
package com.sailmi.system.user.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sailmi.core.mp.base.BaseService;
import com.sailmi.system.entity.Result;
import com.sailmi.system.user.entity.UcsAccountuser;
import com.sailmi.system.user.entity.User;
import com.sailmi.system.user.entity.UserInfo;
import com.sailmi.system.user.excel.UserExcel;


import java.util.List;

/**
 * 服务类
 *
 * @author Chill
 */
public interface IUserService extends BaseService<User> {

	/**
	 * 注册用户
	 * @param user
	 * @return
	 */
	String registerUser(UcsAccountuser user);

	/**
	 * 注册用户 不用短信认证
	 * @param user
	 * @return
	 */
	String registerUserV2(UcsAccountuser user);

	UserInfo userInfoV2(String tenantId, String account, String password);

	UserInfo editKnowniotUser(UcsAccountuser user) throws Exception;

	String delKnowniotUser(Long userId, boolean isSoft) throws Exception;

	/**
	 * 新增或修改用户
	 *
	 * @param user
	 * @return
	 */
	boolean submit(User user);

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param user
	 * @return
	 */
	IPage<User> selectUserPage(IPage<User> page, User user);

	/**
	 * 用户信息
	 *
	 * @param userId
	 * @return
	 */
	UserInfo userInfo(Long userId);

	/**
	 * 用户信息
	 * @param account
	 * @param password
	 * @return
	 */
	UserInfo userInfo( String tenantId, String account, String password);

	/**
	 * 给用户设置角色
	 *
	 * @param userIds
	 * @param roleIds
	 * @return
	 */
	boolean grant(String userIds, String roleIds);

	/**
	 * 初始化密码
	 *
	 * @param userIds
	 * @return
	 */
	boolean resetPassword(String userIds);

	/**
	 * 修改密码
	 *
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @param newPassword1
	 * @return
	 */
	boolean updatePassword(Long userId, String oldPassword, String newPassword, String newPassword1);

	/**
	 * 获取角色名
	 *
	 * @param roleIds
	 * @return
	 */
	List<String> getRoleName(String roleIds);

	/**
	 * 获取部门名
	 *
	 * @param deptIds
	 * @return
	 */
	List<String> getDeptName(String deptIds);

	/**
	 * 导入用户数据
	 *
	 * @param data
	 * @return
	 */
	void importUser(List<UserExcel> data);

	/**
	 * 获取导出用户数据
	 *
	 * @param queryWrapper
	 * @return
	 */
	List<UserExcel> exportUser(Wrapper<User> queryWrapper);

	/**
	 * 根据用户id查询role信息
	 * @param id
	 * @return
	 */
	List<String> queryUserRoles(String id,String defaultEnterpriseId,String tenantId);

	/**
	 *
	 * @param userPhone
	 * @return
	 */
	Boolean sendPhoneCode(String userPhone);

	/**
	 * 验证手机号唯一
	 * @param userPhone
	 * @return
	 */
	int queryUnikePhone(String userPhone);

	Long submitUser(User user);

	int resetUserPassById(String id);

	Result updatePassword1(String userPhone, String userEmail, String password);

	Result checkPhoneCode(String userPhone, String code);

	/**
	 * <p>Description: </p>
	 *
	 * 验证手机号是否存在于系统中.  由于有台接口更改,该接口仅用于忘记密码内的校验
	 * @return: com.sailmi.system.entity.Result
	 * @Author: syt
	 * @Date: 2020/11/12/0012 16:20
	 */
	Result queyrUniquePhone(String userPhone);
}
