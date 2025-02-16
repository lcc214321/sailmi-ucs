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
package com.sailmi.gateway.provider;

import com.sailmi.core.launch.constant.TokenConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 鉴权配置
 *
 * @author Chill
 */
public class AuthProvider {

	public static String TARGET = "/**";
	public static String REPLACEMENT = "";
	public static String AUTH_KEY = TokenConstant.HEADER;
	private static List<String> defaultSkipUrl = new ArrayList<>();

	static {
		defaultSkipUrl.add("/register");
		defaultSkipUrl.add("/sendPhoneCode");
		defaultSkipUrl.add("/uniquePhone");
		defaultSkipUrl.add("/example");
		defaultSkipUrl.add("/token/**");
		defaultSkipUrl.add("/captcha/**");
		defaultSkipUrl.add("/actuator/health/**");
		defaultSkipUrl.add("/v2/api-docs/**");
		defaultSkipUrl.add("/v2/api-docs-ext/**");
		defaultSkipUrl.add("/auth/**");
		defaultSkipUrl.add("/log/**");
		defaultSkipUrl.add("/menu/routes");
		defaultSkipUrl.add("/menu/auth-routes");
		defaultSkipUrl.add("/order/create/**");
		defaultSkipUrl.add("/storage/deduct/**");
		defaultSkipUrl.add("/error/**");
		defaultSkipUrl.add("/assets/**");
		defaultSkipUrl.add("/updatePass");
		defaultSkipUrl.add("/upload/uploadPicture");
		defaultSkipUrl.add("/realuser/IDCard");
		defaultSkipUrl.add("/security/uploadHeadImg");
		defaultSkipUrl.add("/checkCode");
		defaultSkipUrl.add("/file/**");
		defaultSkipUrl.add("/content/**");
		defaultSkipUrl.add("/type/**");
		/**
		 * knowniot - public house
		 * alice
		 */
		defaultSkipUrl.add("/alice/**");
		defaultSkipUrl.add("/wechat/**");
		defaultSkipUrl.add("/oss/endpoint/alice/put-file");
	}

	/**
	 * 默认无需鉴权的API
	 */
	public static List<String> getDefaultSkipUrl() {
		return defaultSkipUrl;
	}

}
