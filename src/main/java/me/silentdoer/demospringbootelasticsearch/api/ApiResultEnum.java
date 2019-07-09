package me.silentdoer.demospringbootelasticsearch.api;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public enum ApiResultEnum {

    /**
     * 处理成功
     */
    SUCCESS(10001, "处理成功"),

    /**
     * 系统异常（非业务异常）
     */
    SYSTEM_ERROR(20002, "系统异常"),

    /**
     * 业务处理异常不弹窗（包括参数错误，检测无效等已预先预知的异常）
     */
    BUSINESS_EXCEPTION(30003, "业务异常"),

    /**
     * 业务处理异常并弹窗
     */
    BUSINESS_EXCEPTION_HUB(30004, "通用异常并hub弹窗");

    private int code;

    private String message;

    ApiResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 返回默认文案和数据
     */
    public <T> ApiResult<T> getResult(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(this.code);
        result.setMsg(this.message);
        result.setData(data);
        return result;
    }

    /**
     * 返回指定文案和数据
     */
    public <T> ApiResult<T> getResult(String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setMsg(message);
        result.setData(data);
        return result;
    }

    /**
     * 返回默认信息
     */
    public ApiResult<Object> getResult() {
        return getResult(null);
    }
}
