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
package com.sailmi.enterprise.service;

import com.sailmi.system.entity.EnterpriseDetails;
import com.sailmi.system.vo.EnterpriseDetailsVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 企业详细信息表 服务类
 *
 * @author sailmi
 * @since 2020-10-23
 */
public interface IEnterpriseDetailsService extends IService<EnterpriseDetails> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param enterpriseDetails
	 * @return
	 */
	IPage<EnterpriseDetailsVO> selectEnterpriseDetailsPage(IPage<EnterpriseDetailsVO> page, EnterpriseDetailsVO enterpriseDetails);

	/***
	 * <p>Description: 企业详细信息</p>
	 *
	 * @param enterpriseDetails:
	 * @return: com.sailmi.system.entity.EnterpriseDetails
	 * @Author: syt
	 * @Date: 2020/10/23/0023 16:59
	 */
	int saveDetail(EnterpriseDetails enterpriseDetails);
}
