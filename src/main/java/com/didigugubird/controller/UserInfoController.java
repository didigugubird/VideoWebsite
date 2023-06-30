package com.didigugubird.controller;

import com.didigugubird.dto.Result;
import com.didigugubird.dto.UploadDTO;
import com.didigugubird.dto.UploadResult;
import com.didigugubird.dto.UserInfoDTO;
import com.didigugubird.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/Userinfo/")
public class UserInfoController {

    @Resource
    UserInfoService userInfoService;

    @PostMapping("Avatar")
    public UploadResult<String> updateAvatar(
            @RequestPart("uploadDTO") UploadDTO uploadDTO,
            @RequestParam("file") MultipartFile avatar,
            UriComponentsBuilder builder) {
        return new UploadResult<>(userInfoService.updateAvatar(uploadDTO, avatar, builder));
    }

    @PostMapping("setUserInfo")
    public Result setUserInfo(UserInfoDTO userInfoDTO) {
        return userInfoService.setUserInfo(userInfoDTO);
    }

}
