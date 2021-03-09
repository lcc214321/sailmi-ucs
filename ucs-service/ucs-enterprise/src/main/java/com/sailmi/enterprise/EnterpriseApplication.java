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
package com.sailmi.enterprise;

import com.sailmi.core.launch.AppLauncher;
import com.sailmi.core.launch.constant.LaunchConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 系统模块启动器
 * @author Chill
 */
@SpringCloudApplication
@EnableFeignClients(LaunchConstant.BASE_PACKAGES)
@ComponentScan(basePackages = {"com.sailmi.**"})
@MapperScan({"com.sailmi.**.mapper.**", "com.sailmi.**.mapper.**"})
public class EnterpriseApplication {

	public static void main(String[] args) {
		AppLauncher.run("sailmi-enterprise", EnterpriseApplication.class, args);
	}

}

