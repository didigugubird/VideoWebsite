package com.didigugubird.service;


import com.didigugubird.dto.Result;
import com.didigugubird.dto.UploadDTO;
import com.didigugubird.dto.UserInfoDTO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

public interface UserInfoService {

    String updateAvatar(UploadDTO uploadDTO, MultipartFile avatar, UriComponentsBuilder builder);

    Result setUserInfo(UserInfoDTO userInfoDTO);

}
