/*
 * Copyright 2015-2020 reserved by jf61.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xinmy.springbootbase.context;

import java.util.Date;

/**
 *
 * @desc
 */
public interface Context {
	//
	String currentToken();

	//
	IUser currentUser();

	// 是否已登录.
	boolean isAuthorized();

	//
	IUser asUser(IUser user);

	void setToken(String action, String token);

	String getToken(String action);

	Date getLastAccessTime();

	Date getLoginTime();

	void setLoginTime(Date loginTime);

	double getLongitude();

	void setLongitude(double longitude);

	double getLatitude();

	void setLatitude(double latitude);
}
