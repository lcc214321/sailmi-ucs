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
package com.sailmi.system.wrapper;

import com.sailmi.common.constant.CommonConstant;
import com.sailmi.core.mp.support.BaseEntityWrapper;
import com.sailmi.core.tool.node.ForestNodeMerger;
import com.sailmi.core.tool.node.INode;
import com.sailmi.core.tool.utils.BeanUtil;
import com.sailmi.core.tool.utils.Func;
import com.sailmi.core.tool.utils.SpringUtil;
import com.sailmi.system.entity.Role;
import com.sailmi.system.service.IRoleService;
import com.sailmi.system.vo.RoleVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author Chill
 */
public class RoleWrapper extends BaseEntityWrapper<Role, RoleVO> {

	private static IRoleService roleService;

	static {
		roleService = SpringUtil.getBean(IRoleService.class);
	}

	public static RoleWrapper build() {
		return new RoleWrapper();
	}

	@Override
	public RoleVO entityVO(Role role) {
		RoleVO roleVO = BeanUtil.copy(role, RoleVO.class);
		if (Func.equals(role.getParentId(), CommonConstant.TOP_PARENT_ID)) {
			roleVO.setParentName(CommonConstant.TOP_PARENT_NAME);
		} else {
			Role parent = roleService.getById(role.getParentId());
			if(parent!=null && parent.getRoleName()!=null) {
				roleVO.setParentName(parent.getRoleName());
			}
		}
		return roleVO;
	}

	public List<INode> listNodeVO(List<Role> list) {
		List<INode> collect = list.stream().map(this::entityVO).collect(Collectors.toList());
		System.out.println(collect);
		if(collect!=null && collect.size()>0) {
			return ForestNodeMerger.merge(collect);
		}else{
			return null;
		}
	}

}
