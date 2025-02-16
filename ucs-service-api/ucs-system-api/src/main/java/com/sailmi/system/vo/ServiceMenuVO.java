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

import com.sailmi.system.entity.ServiceMenu;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 服务功能包设定，表明此功能属于哪个服务包，只有享受此服务包的用户才能访问这个功能视图实体类
 *
 * @author sailmi
 * @since 2020-09-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ServiceMenuVO对象", description = "服务功能包设定，表明此功能属于哪个服务包，只有享受此服务包的用户才能访问这个功能")
public class ServiceMenuVO extends ServiceMenu {
	private static final long serialVersionUID = 1L;

}
