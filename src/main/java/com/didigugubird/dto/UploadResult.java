package com.didigugubird.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UploadResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public UploadResult(T data) {
        this.data = data;
        this.code = 0;
        this.msg = "请求成功";
    }

    public UploadResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
