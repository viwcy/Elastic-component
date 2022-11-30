package com.viwcy.search.common;

/**
 * TODO //、、Controller层响应状态码
 *
 * <p> Title: ResultCode </p>
 * <p> Description: ResultCode </p>
 * <p> History: 2020/9/4 23:02 </p>
 * <pre>
 *      Copyright: Create by FQ, ltd. Copyright(©) 2020.
 * </pre>
 * Author  FQ
 * Version 0.0.1.RELEASE
 */
public enum ResultCode {

    // 成功
    RESULT_SUCCESS(200, "success"),
    // 失败
    RESULT_FAILURE(101, "fail"),
    // 异常
    RESULT_ERROR(500, "error");

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
