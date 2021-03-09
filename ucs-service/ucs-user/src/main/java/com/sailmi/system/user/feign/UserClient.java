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
package com.sailmi.system.user.feign;

import com.sailmi.system.user.entity.User;
import lombok.AllArgsConstructor;
import com.sailmi.core.tool.api.R;
import com.sailmi.system.user.entity.UserInfo;
import com.sailmi.system.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务Feign实现类
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
public class UserClient implements IUserClient {
	@Autowired
	private IUserService service;

	@Override
	@GetMapping(API_PREFIX + "/user-info-by-id")
	public R<UserInfo> userInfo(Long userId) {
		return R.data(service.userInfo(userId));
	}


	@Override
	@GetMapping(API_PREFIX + "/user-info")
	public R<UserInfo> userInfo(String tenantId,String account, String password) {
		return R.data(service.userInfo(tenantId,account, password));
	}

	@Override
	@PostMapping(API_PREFIX + "/submit-user")
	public Long  submitUserInfo(@RequestBody User user) {
		return service.submitUser(user);
	}

	@Override
	@PostMapping(API_PREFIX + "/reset-userpass")
	public Boolean resetUserPass(String id) {
		boolean flag=false;
		int i=service.resetUserPassById(id);
		if(i>0){
			flag=true;
		}
		return flag;
	}

}
