package me.silentdoer.demospringbootelasticsearch.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiResult<T> {

    private int code;

    private String msg;

    /**
     * 第三方返回的code值
     */
    private int thirdCode;

    /**
     * 第三方返回的msg值（可拼接）
     */
    private String thirdMsg;

    private T data;

    public ApiResult(T data) {
        this.code = ApiResultEnum.SUCCESS.getCode();
        this.msg = ApiResultEnum.SUCCESS.getMessage();
        this.data = data;
    }

    public ApiResult() {
        this(null);
    }

    /**
     * 10000->19999之间的值都属于正常值，类似HTTP里的200、203之类的
     */
    public boolean isNormalResult() {
        return this.code >= 10000 && this.code < 20000;
    }

    public ApiResult<Object> commonFail(String message) {
        return ApiResult.builder().code(ApiResultEnum.BUSINESS_EXCEPTION.getCode()).msg(message)
                .data(null).build();
    }

    public ApiResult<Object> commonFail(String message, Integer thirdCode) {
        return ApiResult.builder().code(ApiResultEnum.BUSINESS_EXCEPTION.getCode()).msg(message)
                .data(null).thirdCode(thirdCode).build();
    }

    public ApiResult<Object> commonFail(String message, String thirdMsg) {
        return ApiResult.builder().code(ApiResultEnum.BUSINESS_EXCEPTION.getCode()).msg(message)
                .data(null).thirdMsg(thirdMsg).build();
    }

    public ApiResult<Object> commonFail(String message, Integer thirdCode, String thirdMsg) {
        return ApiResult.builder().code(ApiResultEnum.BUSINESS_EXCEPTION.getCode()).msg(message)
                .data(null).thirdCode(thirdCode).thirdMsg(thirdMsg).build();
    }
}