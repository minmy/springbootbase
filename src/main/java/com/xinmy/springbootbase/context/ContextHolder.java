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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @desc
 */
public class ContextHolder {

	private static final Map<String, Context> ctxRepo	= new HashMap<>();				//
	private static ThreadLocal<Context> tl		= new ThreadLocal<Context>();

	public static void appendContext(final Context context) {
		ctxRepo.put(context.currentToken(), context);
	}

	//
	public static Context currentContext() {
		return tl.get();
	}

	public static void bindContext(final Context context) {
		tl.set(context);
	}

	public static void unbindContext() {
		tl.remove();
	}

	private ContextHolder() {
		super();
	}

	public static Context contextOf(final String token) {
		return ctxRepo.get(token);
	}

	public static void remove(final String token) {
		ctxRepo.remove(token);
	}

	public static Context isOnline(final IUser user, Class<? extends IUser> clazz) {
		Long tid = user.getId();
		Optional<Context> opt = ctxRepo.entrySet().stream().map(e -> e.getValue())
		        .filter(c -> null != c && null != c.currentUser() && null != c.currentUser().getId())
		        .filter(c -> clazz.isAssignableFrom(c.currentUser().getClass()))
		        .filter(c -> Long.compare(c.currentUser().getId(), tid) == 0) //
		        .findFirst();
		//
		return opt.isPresent() ? opt.get() : null;
	}

	public static Map<String, Context> contexts() {
		return new HashMap<>(ctxRepo);
	}
}
