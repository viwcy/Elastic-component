package com.viwcy.search.common;

/**
 * TODO //
 *
 * <p> Title: BaseController </p>
 * <p> Description: BaseController </p>
 * <p> History: 2020/9/4 23:02 </p>
 * <pre>
 *      Copyright: Create by FQ, ltd. Copyright(©) 2020.
 * </pre>
 * Author  FQ
 * Version 0.0.1.RELEASE
 */
public class BaseController {

    public BaseController() {
    }

    public ResultEntity success() {
        return new ResultEntity(ResultCode.RESULT_SUCCESS.getCode(), ResultCode.RESULT_SUCCESS.getMessage(), null);
    }

    public <T> ResultEntity<T> success(T data) {
        return new ResultEntity(ResultCode.RESULT_SUCCESS.getCode(), ResultCode.RESULT_SUCCESS.getMessage(), data);
    }

    public <T> ResultEntity<T> success(String message) {
        return new ResultEntity(ResultCode.RESULT_SUCCESS.getCode(), message, null);
    }

    public <T> ResultEntity<T> success(String message, T data) {
        return new ResultEntity(ResultCode.RESULT_SUCCESS.getCode(), message, data);
    }

    public <T> ResultEntity<T> fail() {
        return new ResultEntity(ResultCode.RESULT_FAILURE.getCode(), ResultCode.RESULT_FAILURE.getMessage(), null);
    }

    public <T> ResultEntity<T> fail(String message) {
        return new ResultEntity(ResultCode.RESULT_FAILURE.getCode(), message, null);
    }

    public <T> ResultEntity<T> error() {
        return new ResultEntity(ResultCode.RESULT_ERROR.getCode(), ResultCode.RESULT_ERROR.getMessage(), null);
    }

    public <T> ResultEntity<T> error(String message) {
        return new ResultEntity(ResultCode.RESULT_ERROR.getCode(), message, null);
    }

}
