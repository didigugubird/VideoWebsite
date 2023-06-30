package com.didigugubird.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

import static com.didigugubird.utils.SystemConstants.JWT_KEY;
import static com.didigugubird.utils.SystemConstants.JWT_TTL;

@Slf4j
public class JWTUtils {

    public static String createToken(String uid) {

        DateTime SetTime = DateTime.now();
        DateTime expireTime = SetTime.offsetNew(DateField.HOUR, JWT_TTL);
        HashMap<String, Object> map = new HashMap<>();

        map.put(JWTPayload.NOT_BEFORE, SetTime);
        map.put(JWTPayload.ISSUED_AT, SetTime);
        map.put(JWTPayload.EXPIRES_AT, expireTime);

        //载荷
        map.put("id", uid);
        return JWTUtil.createToken(map, JWT_KEY.getBytes());
    }

    public static Boolean parseJWT(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        if (!jwt.setKey(JWT_KEY.getBytes()).verify()) {
            log.error("token: " + token + " 异常");
        }
        if (!jwt.validate(0)) {
            log.error("token: " + token + " 已过期");
        }

        return true;
    }

}
