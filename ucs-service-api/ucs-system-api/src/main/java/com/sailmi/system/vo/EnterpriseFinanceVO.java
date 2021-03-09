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
package com.sailmi.system.vo;

import com.sailmi.system.entity.EnterpriseFinance;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 企业财务信息表视图实体类
 *
 * @author sailmi
 * @since 2020-10-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EnterpriseFinanceVO对象", description = "企业财务信息表")
public class EnterpriseFinanceVO extends EnterpriseFinance {
	private static final long serialVersionUID = 1L;

}
