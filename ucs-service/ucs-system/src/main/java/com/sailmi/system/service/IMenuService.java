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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sailmi.core.secure.AuthUser;
import com.sailmi.core.tool.support.Kv;
import com.sailmi.system.entity.Menu;
import com.sailmi.system.entity.MenuTreeResultEntity;
import com.sailmi.system.vo.MenuVO;

import java.util.List;

/**
 * 服务类
 *
 * @author Chill
 */
public interface IMenuService extends IService<Menu> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param menu
	 * @return
	 */
	IPage<MenuVO> selectMenuPage(IPage<MenuVO> page, MenuVO menu);

	/**
	 * 菜单树形结构
	 *
	 * @param roleId
	 * @return
	 */
	List<MenuVO> routes(String roleId);

	/**
	 * 按钮树形结构
	 *
	 * @param roleId
	 * @return
	 */
	List<MenuVO> buttons(String roleId);

	/**
	 * 树形结构
	 *
	 * @return
	 */
	List<MenuVO> tree();

	/**
	 * 授权树形结构
	 *
	 * @param user
	 * @return
	 */
	List<MenuVO> grantTree(AuthUser user);

	/**
	 * 默认选中节点
	 *
	 * @param roleIds
	 * @return
	 */
	List<String> roleTreeKeys(String roleIds);

	/**
	 * 获取配置的角色权限
	 *
	 * @param user
	 * @return
	 */
	List<Kv> authRoutes(AuthUser user);

	List<MenuVO> grantServiceMenuTree(AuthUser user);


}
