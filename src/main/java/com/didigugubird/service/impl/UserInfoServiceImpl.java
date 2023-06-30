package com.didigugubird.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.didigugubird.dto.Result;
import com.didigugubird.dto.UploadDTO;
import com.didigugubird.dto.UserInfoDTO;
import com.didigugubird.mapper.UserInfoMapper;
import com.didigugubird.pojo.UserInfo;
import com.didigugubird.service.UserInfoService;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;

import static com.didigugubird.utils.SystemConstants.AVATAR_PATH;

@Slf4j
@Service
@CacheConfig(cacheNames = "users_info")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    UserInfoMapper userInfoMapper;

    @SneakyThrows
    @Override
    public String updateAvatar(UploadDTO uploadDTO, MultipartFile avatar, UriComponentsBuilder builder) {

        int uid = uploadDTO.getUid();
        String filePath = AVATAR_PATH + avatar.getOriginalFilename();

        File file = new File(filePath);

        //写入到磁盘
        @Cleanup
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        StreamUtils.copy(avatar.getBytes(), fileOutputStream);

        //将头像写入到用户信息中
        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("avatar", filePath);
        userInfoMapper.update(null, updateWrapper);

        return builder.path(filePath).build().toUriString();
    }

    @Override
    public Result setUserInfo(UserInfoDTO userInfoDTO) {

        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", userInfoDTO.getUid());
        updateWrapper.set("nick_name", userInfoDTO.getNickName());
        updateWrapper.set("sign", userInfoDTO.getSign());
        updateWrapper.set("sex", userInfoDTO.getSex());
        updateWrapper.set("birth_date", userInfoDTO.getBirthDate());
        userInfoMapper.update(null, updateWrapper);

        return null;
    }
}