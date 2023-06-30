package com.didigugubird.mysecuritylogin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didigugubird.dto.SecurityPhoneLoginDetails;
import com.didigugubird.mapper.UserMapper;
import com.didigugubird.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Resource;
import java.util.Objects;

import static com.didigugubird.utils.RedisConstants.LOGIN_CODE_KEY;

@Slf4j
public class PhoneCodeAuthenticationProvider implements AuthenticationProvider {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    private PhoneUserDetailsServiceImpl userDetailsService;
    @Resource
    private UserMapper userMapper;

    public void setUserDetailsService(PhoneUserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        PhoneCodeAuthenticationToken token = (PhoneCodeAuthenticationToken) authentication;
        String phone = (String) token.getPrincipal();
        String code = (String) token.getCredentials();

        String redisCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if (!code.equals(redisCode)) {
            log.error("验证码错误");
            return null;
        }

        //验证码正确，去数据库中获取用户，判断是否注册过
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = userMapper.selectOne(queryWrapper);

        //数据库中没有，就进行注册
        if (Objects.isNull(user)) {
            user = new User();
            user.setPhone(phone);
            userMapper.insert(user);
        }
        SecurityPhoneLoginDetails securityPhoneLoginDetails = new SecurityPhoneLoginDetails(user);
        //登录成功后,删除验证码
        stringRedisTemplate.delete(LOGIN_CODE_KEY + phone);
        return new PhoneCodeAuthenticationToken(securityPhoneLoginDetails, null, securityPhoneLoginDetails.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PhoneCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
