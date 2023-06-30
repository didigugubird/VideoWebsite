package com.didigugubird.cofig;

import com.didigugubird.mysecuritylogin.EmailUserDetailsServiceImpl;
import com.didigugubird.mysecuritylogin.PhoneCodeAuthenticationProvider;
import com.didigugubird.mysecuritylogin.PhoneUserDetailsServiceImpl;
import com.didigugubird.mysecuritylogin.TokenAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] AUTH_LIST = {
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-ui/*",
            "/upload/*",
            "/*/*",
            "/user/*",
            "/*"
    };
    @Resource
    TokenAuthenticationFilter tokenAuthenticationFilter;
    @Resource
    private PhoneUserDetailsServiceImpl phoneUserDetailsService;
    @Resource
    private EmailUserDetailsServiceImpl emailUserDetailsService;

    @Bean
    public PhoneCodeAuthenticationProvider phoneCodeAuthenticationProvider() {
        PhoneCodeAuthenticationProvider phoneCodeAuthenticationProvider = new PhoneCodeAuthenticationProvider();
        phoneCodeAuthenticationProvider.setUserDetailsService(phoneUserDetailsService);
        return phoneCodeAuthenticationProvider;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(emailUserDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(phoneCodeAuthenticationProvider());
        authenticationProviders.add(daoAuthenticationProvider());
        return new ProviderManager(authenticationProviders);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 关闭csrf
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(AUTH_LIST).permitAll()
                .antMatchers("/login/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .cors()
                .configurationSource(this.configurationSource())
                .and()
                .rememberMe()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;
    }

    private CorsConfigurationSource configurationSource(){
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.addExposedHeader("*");
        cors.setAllowCredentials(true);
        cors.addAllowedOriginPattern("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;


    }
}
