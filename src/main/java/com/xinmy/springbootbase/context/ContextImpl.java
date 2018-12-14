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

import com.xinmy.springbootbase.helper.StringUtils;
import com.xinmy.springbootbase.interceptor.ContextInjectInterceptor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc
 */
public class ContextImpl implements Context {
    private String token;                                            // 令牌
    private IUser               user;                                            // 会员
    private Date lastAccessTime;                                    // 当前访问时间
    private Map<String, String> actionTokens = new HashMap<String, String>();// 动作令牌
    private Date loginTime;                                        // 登录时间
    private double              longitude;                                        // 经度
    private double              latitude;                                        // 纬度

    public ContextImpl(final String token, final IUser user) {
        this.token = token;
        this.user = user;
    }

    @Override
    public String currentToken() {
        return token;
    }

    @Override
    public IUser currentUser() {
        return user;
    }

    @Override
    public boolean isAuthorized() {
        return ((user != null) && (null != user.getId())) || StringUtils.isEquals(token, ContextInjectInterceptor.SWAGGER_TOKEN);
    }

    @Override
    public IUser asUser(final IUser user) {
        IUser tmp = this.user;
        this.user = user;
        return tmp;
    }

    @Override
    public void setToken(final String action, final String token) {
        actionTokens.put(action, token);
    }

    @Override
    public String getToken(final String action) {
        return actionTokens.get(action);
    }

    @Override
    public Date getLoginTime() {
        return loginTime;
    }

    @Override
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    @Override
    public Date getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
