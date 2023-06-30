package com.didigugubird.service;

import com.didigugubird.dto.EmailRegisterDTO;
import com.didigugubird.dto.Result;
import com.didigugubird.dto.SecurityEmailLoginDetails;
import com.didigugubird.dto.SecurityPhoneLoginDetails;

import javax.servlet.http.HttpServletRequest;


public interface UserService {

    Result SecurityEmailLogin(SecurityEmailLoginDetails emailLoginDetails);

    Result SecurityPhoneLogin(SecurityPhoneLoginDetails phoneDetails);

    Result EmailRegister(EmailRegisterDTO emailRegisterDTO);

    Result emailCode(String email);

    //发送验证码
    Result phoneCode(String phone);

    //设置密码
    Result setPassWord(String password, int uid);

    Result setEmail(String Email, int uid);

    Result logout(HttpServletRequest request);
}
