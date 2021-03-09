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
package com.sailmi.system.user.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import com.sailmi.system.user.entity.User;
import com.sailmi.system.user.excel.UserExcel;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author Chill
 */
public interface UserMapper extends BaseMapper<User> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param user
	 * @return
	 */
	List<User> selectUserPage(IPage page, User user);

	/**
	 * 获取用户
	 * @param account
	 * @param password
	 * @return
	 */
	User getUser(@Param("tenantId") String tenantId,@Param("account") String account, @Param("password") String password);

	/**
	 * 获取角色名
	 *
	 * @param ids
	 * @return
	 */
	List<String> getRoleName(String[] ids);

	/**
	 * 获取角色别名
	 *
	 * @param ids
	 * @return
	 */
	List<String> getRoleAlias(String... ids);

	/**
	 * 获取部门名
	 *
	 * @param ids
	 * @return
	 */
	List<String> getDeptName(String[] ids);

	/**
	 * 获取导出用户数据
	 *
	 * @param queryWrapper
	 * @return
	 */
	List<UserExcel> exportUser(@Param("ew") Wrapper<User> queryWrapper);

	List<String> queryUserRoles(@Param("id") String id, @Param("defaultEnterpriseId") String defaultEnterpriseId);

	String getTenantByEnterpriseId(String EnterpriseId); //erro by yzh

	User getConsoleUser(@Param("account")String account, @Param("password")String password);

	/**
	 @Insert({"INSERT INTO ucs_user(tenant_id,login_name,password,default_enterprise,create_user,is_deleted) VALUES (#{tenantId},#{account},#{password},#{defaultEnterpriseId},#{createUser},#{isDeleted})"})
	 @Options(useGeneratedKeys = true, keyProperty = "id")
	 @Insert("INSERT INTO ucs_user(tenant_id,login_name,password,default_enterprise,create_user,is_deleted) VALUES (#{user.tenantId},#{user.account},#{user.password},#{user.defaultEnterpriseId},#{user.createUser},#{user.isDeleted})")
	 @Options(useGeneratedKeys = true, keyProperty = "user.id",keyColumn="id")
	 * @param user
	 * @return
	 */

	int submitUserInfo(User user);

	int resetUserPass(@Param("id")String id,@Param("pass")String pass);

	String queryUserIdByPhone(String userPhone);

	String queryUserIdByEmail(String userEmail);

	int updateUserPass(@Param("userId")String userId,@Param("password") String password);

	int queryPhoneNum(String userPhone);
}
