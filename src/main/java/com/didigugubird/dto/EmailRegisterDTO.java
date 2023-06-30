package com.didigugubird.dto;

import lombok.Data;

@Data
public class EmailRegisterDTO {
    private String email;
    private String passWord;
    private String code;
}
