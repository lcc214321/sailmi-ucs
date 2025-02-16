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


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.sailmi.core.secure.AuthUser;
import com.sailmi.system.entity.ServiceEntity;
import com.sailmi.system.vo.ServiceVO;

import java.util.List;

/**
 * 可提供的服务清单，企业可以通过服务清单 服务类
 *
 * @author sailmi
 * @since 2020-09-09
 */
public interface IServiceService extends IService<ServiceEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param service
	 * @return
	 */
	IPage<ServiceVO> selectServicePage(IPage<ServiceVO> page, ServiceVO service);

	List<String> serviceTreeKeys(String serviceId);

	boolean grant(String serviceId, List<Long> menuIds);

	List<ServiceVO> grantTree(AuthUser authUser);


}
