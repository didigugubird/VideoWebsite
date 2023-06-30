package com.didigugubird.controller;

import com.didigugubird.dto.EmailRegisterDTO;
import com.didigugubird.dto.Result;
import com.didigugubird.dto.SecurityEmailLoginDetails;
import com.didigugubird.dto.SecurityPhoneLoginDetails;
import com.didigugubird.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/login/")
public class UserController {
    @Resource
    private UserServiceImpl userServiceImpl;

    @PostMapping("phoneCode")
    public Result phoneCode(@RequestParam("phoneNumber") String phone) {
        return userServiceImpl.phoneCode(phone);
    }

    @PostMapping("emailCode")
    public Result emailCode(@RequestParam("email") String email) {
        return userServiceImpl.emailCode(email);
    }

    @PostMapping("register")
    public Result emailRegister(@RequestBody EmailRegisterDTO emailRegisterDTO) {
        return userServiceImpl.EmailRegister(emailRegisterDTO);
    }

    @PostMapping("emailLogin")
    public Result loginWithSeEmail(@RequestBody SecurityEmailLoginDetails securityEmailLoginDetails) {
        return userServiceImpl.SecurityEmailLogin(securityEmailLoginDetails);
    }

    @PostMapping("phoneLogin")
    public Result loginWithSePhone(@RequestBody SecurityPhoneLoginDetails securityPhoneLoginDetails) {
        return userServiceImpl.SecurityPhoneLogin(securityPhoneLoginDetails);
    }

    @PostMapping("setEmail")
    public Result setEmail(@RequestParam("email") String email, @RequestParam("uid") int uid) {
        return userServiceImpl.setEmail(email, uid);
    }

    @PostMapping("setPassword")
    public Result setPassword(@RequestParam("password") String password, @RequestParam("uid") int uid) {
        return userServiceImpl.setPassWord(password, uid);
    }

    @RequestMapping("logout")
    public Result logout(HttpServletRequest request) {
        return userServiceImpl.logout(request);
    }

}
