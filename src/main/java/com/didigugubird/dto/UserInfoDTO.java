package com.didigugubird.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UserInfoDTO {
    private Integer uid;

    private String nickName;

    private String avatar;

    private Integer exp;

    private Integer coins;

    private Integer likes;

    private Integer playTimes;

    private Integer fanNumbers;

    private Integer followerNumbers;

    private String birthDate;

    private String sex;

    private String sign;
}
