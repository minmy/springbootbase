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
package com.xinmy.springbootbase.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @desc
 */
@ApiModel(value = "操作结果")
public class Result<T> {

    public static final Result  SUCCESS = new Result(true, "操作成功", null);
    public static final Result  FAIL    = new Result(false);
    @ApiModelProperty(value = "结果: true=操作成功, false=操作失败", allowableValues = "true,false", required = true)
    private             boolean success;
    @ApiModelProperty(value = "提示信息")
    private String message;
    @ApiModelProperty(value = "操作失败时的异常信息")
    private String errorMsg;
    @ApiModelProperty(value = "操作结果：附带的数据")
    private             T       data;

    public Result(final boolean success) {
        this.setSuccess(success);
    }

    public Result(final boolean success, final T data) {
        this.setSuccess(success);
        this.setData(data);
    }

    public Result(final boolean success, final String message, final String errorMsg, final T data) {
        this.setSuccess(success);
        this.setData(data);
        this.setMessage(message);
        this.setErrorMsg(errorMsg);
    }

    public Result(final boolean success, final String message, final String errorMsg) {
        this.setSuccess(success);
        this.setMessage(message);
        this.setErrorMsg(errorMsg);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(final String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public int getCode() {
        return success ? 0 : 502;
    }
}
