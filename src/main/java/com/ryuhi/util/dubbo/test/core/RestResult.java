package com.ryuhi.util.dubbo.test.core;

import lombok.Data;

@Data
public class RestResult {
    /**
     * 成功的代码
     */
    private static final int SUCCESS_RESULT = 1;
    /**
     * 默认失败的代码
     */
    private static final int FAILURE_RESULT = -1;
    /**
     * 状态代码
     */
    private Integer status;
    /**
     * 事件消息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private Object result;

    /**
     * 操作成功，只返回数据
     *
     * @param result 返回的数据
     * @return
     */
    public static RestResult successResult(Object result) {
        return successResult("操作成功", result);
    }

    /**
     * 最简单的成功返回
     *
     * @return
     */
    public static RestResult successResult() {
        return successResult("操作成功", null);
    }

    /**
     * 最全的成功返回
     *
     * @param message 成功信息
     * @param result  返回的数据
     * @return
     */
    public static RestResult successResult(String message, Object result) {
        return privateNewRestResult(SUCCESS_RESULT, message, result);
    }

    /**
     * 传回成功信息
     *
     * @param message 成功信息
     * @return
     */
    public static RestResult successResult(String message) {
        return successResult(message, null);
    }

    /**
     * 最简单的失败返回
     *
     * @return
     */
    public static RestResult failureResult() {
        return failureResult(FAILURE_RESULT, "操作失败");
    }

    /**
     * 传回失败信息
     *
     * @param message 失败信息
     * @return
     */
    public static RestResult failureResult(String message) {
        return failureResult(message, null);
    }

    /**
     * 传回失败信息和返回的数据
     *
     * @param message 失败信息
     * @param result  返回的数据
     * @return
     */
    public static RestResult failureResult(String message, Object result) {
        return failureResult(FAILURE_RESULT, message, result);
    }

    /**
     * 最完整的失败信息
     *
     * @param code    错误代码
     * @param message 失败信息
     * @param result  返回的数据
     * @return
     */
    public static RestResult failureResult(int code, String message, Object result) {
        return privateNewRestResult(code, message, result);
    }

    /**
     * 传回错误代码和失败信息
     *
     * @param code    错误代码
     * @param message 失败信息
     * @return
     */
    public static RestResult failureResult(int code, String message) {
        return failureResult(code, message, null);
    }

    private static RestResult privateNewRestResult(int code, String message, Object result) {
        RestResult jr = new RestResult();
        jr.setStatus(code);
        jr.setMsg(message);
        jr.setResult(result);
        return jr;
    }
}
