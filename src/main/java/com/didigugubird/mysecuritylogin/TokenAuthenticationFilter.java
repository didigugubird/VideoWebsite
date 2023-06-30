package com.didigugubird.mysecuritylogin;

import com.didigugubird.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.didigugubird.utils.RedisConstants.LOGIN_USER_TOKEN;

@Component
@WebFilter
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("token");

        if (!Objects.isNull(token)) {
            try {
                JWTUtils.parseJWT(token);
            } catch (Exception e) {
                log.error("token 异常", e);
                throw e;
            }
            //从 redis 中获取 token 对应的用户信息
            Object userDetails = redisTemplate.opsForValue().get(LOGIN_USER_TOKEN + token);

            if (Objects.isNull(userDetails)) {
                throw new RuntimeException("用户未登录");
            } else {
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                SecurityContextHolder.getContext().setAuthentication(authRequest);
            }
        }
        filterChain.doFilter(request, response);
    }
}

