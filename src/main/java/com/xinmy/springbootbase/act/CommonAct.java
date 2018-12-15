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
package com.xinmy.springbootbase.act;

import com.xinmy.springbootbase.context.Context;
import com.xinmy.springbootbase.context.ContextHolder;
import com.xinmy.springbootbase.helper.Result;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @desc
 */
public class CommonAct {

	// 获取字段校验不通过的信息
	protected Map<String, String> getErrors(final BindingResult result) {
		Map<String, String> map = new HashMap<String, String>();
		List<FieldError> list = result.getFieldErrors();
		for (FieldError error : list) {
			System.out.println("error.getField():" + error.getField());
			System.out.println("error.getDefaultMessage():" + error.getDefaultMessage());

			map.put(error.getField(), error.getDefaultMessage());
		}
		return map;
	}

	//
	protected final Result error(final BindingResult result) {
		Context context = ContextHolder.currentContext();
		Map<String, String> map = this.getErrors(result);
		String errmsg = map.values().iterator().next();
		return new Result(false, "操作失败", errmsg);
	}
}
