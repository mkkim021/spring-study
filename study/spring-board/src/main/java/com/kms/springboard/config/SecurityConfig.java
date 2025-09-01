package com.kms.springboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean // SecurityFilterChain을 등록해야 스프링이 해당 필터 체인을 HTTP 요청 처리에 적용
            // @Component는 일반 빈을 등록하는 용도이므로 사용 x
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/api/board").permitAll()
                        .requestMatchers("/api/board/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form ->form
                        .loginPage("/users/login")
                        .loginProcessingUrl("/users/login")
                        .defaultSuccessUrl("/api/board",true)
                        .failureUrl("/users/login")
                        .permitAll()
                )
                .csrf(Customizer.withDefaults());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // BCryptPasswordEncoder : 비밀번호를 암호화하는 데 사용할 수 있는 메서드를 가진 클래스
    }

}
