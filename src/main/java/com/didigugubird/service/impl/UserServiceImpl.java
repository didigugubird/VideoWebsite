package com.didigugubird.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.didigugubird.dto.EmailRegisterDTO;
import com.didigugubird.dto.Result;
import com.didigugubird.dto.SecurityEmailLoginDetails;
import com.didigugubird.dto.SecurityPhoneLoginDetails;
import com.didigugubird.mapper.UserMapper;
import com.didigugubird.mysecuritylogin.PhoneCodeAuthenticationToken;
import com.didigugubird.pojo.User;
import com.didigugubird.service.UserService;
import com.didigugubird.utils.JWTUtils;
import com.didigugubird.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.didigugubird.utils.RedisConstants.*;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public Result phoneCode(String phone) {
        // 1.检验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误");
        }
        // 2.生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 3.保存验证码到 redis 中，有效期为5分钟
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 4.发送验证码，接口还没去搞

        return Result.ok();
    }

    @Override
    public Result emailCode(String email) {
        // 1.检验手机号
        if (RegexUtils.isEmailInvalid(email)) {
            return Result.fail("邮箱格式错误");
        }
        // 2.生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 3.发送验证码
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("DGBVideo 邮箱验证码");
            message.setText(code);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败，请检查邮箱地址:{} 是否正确", email);
            return Result.fail("邮箱发送失败");
        }

        // 4.保存验证码到 redis 中，有效期为5分钟
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        return Result.ok();
    }

    /* 走security登录*/
    @Override
    public Result SecurityPhoneLogin(SecurityPhoneLoginDetails phoneDetails) {
        PhoneCodeAuthenticationToken authenticationToken =
                new PhoneCodeAuthenticationToken(phoneDetails.getUsername(), phoneDetails.getCode());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if (Objects.isNull(authentication)) {
            return Result.fail("登录失败,请检查手机号和验证码是否输入正确");
        }

        phoneDetails = (SecurityPhoneLoginDetails) authentication.getPrincipal();
        String userid = phoneDetails.getUser().getUid().toString();
        String token = JWTUtils.createToken(userid);

        //将 token 存到 redis 中
        redisTemplate.opsForValue().set(LOGIN_USER_TOKEN + token, phoneDetails);

        return Result.ok(token);
    }

    @Override
    public Result EmailRegister(EmailRegisterDTO emailRegisterDTO) {

        String email = emailRegisterDTO.getEmail();
        if (RegexUtils.isEmailInvalid(email)) {
            return Result.fail("邮箱格式错误");
        }

        //对密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String pw = passwordEncoder.encode(emailRegisterDTO.getPassWord());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = getOne(queryWrapper);

        if (Objects.nonNull(user)) {
            return Result.fail("邮箱已被使用");
        } else {
            user = new User();
            user.setEmail(email);
            user.setPassword(pw);
            String code = emailRegisterDTO.getCode();
            if (!code.equals(stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + email))) {
                return Result.fail("验证码错误");
            }
            stringRedisTemplate.delete(LOGIN_CODE_KEY + email);
            save(user);
            return Result.ok();
        }
    }

    /**
     * Security邮箱密码登录
     */
    @Override
    public Result SecurityEmailLogin(SecurityEmailLoginDetails emailDetails) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(emailDetails.getUsername(), emailDetails.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if (Objects.isNull(authentication)) {
            return Result.fail("登录失败");
        }

        //生成 token
        emailDetails = (SecurityEmailLoginDetails) authentication.getPrincipal();
        String userid = emailDetails.getUser().getUid().toString();
        String token = JWTUtils.createToken(userid);

        //将token存到 redis 中
        redisTemplate.opsForValue().set(LOGIN_USER_TOKEN + token, emailDetails);
        return Result.ok(token);
    }

    @Override
    public Result setPassWord(String password, int uid) {

        User user = query().eq("uid", uid).one();
        if (null == user) return Result.fail("不存在UID为 " + uid + " 的用户");

        //对密码进行加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String pw = passwordEncoder.encode(password);

        user.setPassword(pw);

        saveOrUpdate(user);

        return Result.ok();
    }

    @Override
    public Result setEmail(String Email, int uid) {

        User user = query().eq("uid", uid).one();
        if (null == user) return Result.fail("不存在UID为 " + uid + " 的用户");
        user.setEmail(Email);
        saveOrUpdate(user);

        return Result.ok();
    }

    @Override
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        redisTemplate.delete(LOGIN_USER_TOKEN + token);

        return Result.ok();
    }
}
