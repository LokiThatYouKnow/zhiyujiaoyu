package com.mengzhidui.a7springboot.response;

import com.mengzhidui.a7springboot.enums.ResponseCode;
import lombok.Data;


import java.io.Serializable;

@Data
public class R<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public R() {
    }

    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(ResponseCode responseCode, String msg) {
        this.code = responseCode.getCode();
        this.msg = msg;
    }

    public R(ResponseCode responseCode, String msg,T data) {
        this.code = responseCode.getCode();
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> success(String msg) {
        return new R<>(ResponseCode.SUCCESS, msg);
    }

    public static <T> R<T> success(String msg,T data) {
        return new R<>(ResponseCode.SUCCESS, msg, data);
    }

    public static <T> R<T> success(Integer code,String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> success(Integer code,String msg,T data) {
        return new R<>(code, msg, data);
    }

    public static <T> R<T> success(ResponseCode responseCode,String msg) {return new R<>(responseCode, msg);}

    public static <T> R<T> success(ResponseCode responseCode,String msg,T data) {return new R<>(responseCode, msg, data);}

    public static <T> R<T> error(String msg) {
        return new R<>(ResponseCode.BAD_REQUEST, msg);
    }

    public static <T> R<T> error(String msg, T data) {
        return new R<>(ResponseCode.BAD_REQUEST, msg, data);
    }

    public static <T> R<T> error(Integer code,String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> error(Integer code,String msg, T data) {
        return new R<>(code, msg, data);
    }

    public static <T> R<T> error(ResponseCode responseCode,String msg) {return new R<>(responseCode, msg);}

    public static <T> R<T> error(ResponseCode responseCode,String msg, T data) {return new R<>(responseCode, msg, data);}


}
