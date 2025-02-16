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
package com.sailmi.system.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sailmi.system.entity.UserRole;
import com.sailmi.system.vo.UserRoleVO;

import java.util.List;

/**
 *  服务类
 *
 * @author sailmi
 * @since 2020-10-23
 */
public interface IUserRoleService extends IService<UserRole> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param userRole
	 * @return
	 */
	IPage<UserRoleVO> selectUserRolePage(IPage<UserRoleVO> page, UserRoleVO userRole);

	List<UserRole> queryUserlistByRoleId(String roleId);
}
