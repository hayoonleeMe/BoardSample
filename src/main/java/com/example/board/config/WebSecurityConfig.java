package com.example.board.config;

import com.example.board.security.JwtAuthorizationFilter;
import com.example.board.security.UserDetailsServiceImpl;
import com.example.board.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * @EnableWebSecurity: 스프링이 기본적으로 제공하는 보안 설정을 무효화하고, 우리가 작성한 WebSecurityConfig 의 필터 체인(Filter Chain) 설정을 애플리케이션에 적용하도록 지시하는 어노테이션이다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /*
     * PasswordEncoder: 사용자가 입력한 비밀번호를 날것(Plain Text) 그대로 데이터베이스에 저장하면 심각한 보안 사고로 이어진다.
     * 이 객체를 스프링 빈(Bean)으로 등록해 두면, 향후 회원가입 로직에서 제공되는 BCrypt 해시 알고리즘을 통해 비밀번호를 안전하게 단방향 암호화할 수 있다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    /*
     * csrf.disable(): CSRF(크로스 사이트 요청 위조)는 브라우저가 제공하는 세션(Session)과 쿠키(Cookie)를 악용하는 공격 방식이다.
     * 우리는 세션 대신 브라우저의 저장소에 보관되는 JWT 를 인증 수단으로 사용할 것이므로 해당 방어 기능을 비활성화한다.
     *
     * SessionCreationPolicy.STATELESS: 스프링 시큐리티는 기본적으로 요청이 들어오면 세션을 생성하여 사용자의 로그인 상태를 유지하려 한다.
     * JWT 의 핵심은 서버가 상태를 기억하지 않는 무상태성(Stateless)에 있으므로, 이 설정을 통해 서버 메모리에 세션을 생성하지 않도록 강제한다.
     *
     * authorizeHttpRequests: URL 경로와 HTTP 메서드에 따라 접근 권한을 세밀하게 설정한다.
     * Swagger 와 관련된 경로, 조회를 위한 GET 요청, 그리고 앞으로 만들 회원가입/로그인 경로는 .permitAll() 로 열어두어 누구나 접근할 수 있게 한다.
     * 반대로 글 작성(POST), 수정(PUT), 삭제(DELETE) 등 명시되지 않은 모든 요청은 .authenticated() 에 의해 인증된 사용자만 통과할 수 있다.
     *
     * addFilterBefore: 스프링 시큐리티의 기본 인증 필터인 UsernamePasswordAuthenticationFilter 가 실행되기 직전에 방금 만든 JwtAuthorizationFilter 가 먼저 실행되도록 끼워 넣는다.
     * 즉, 아이디와 비밀번호로 검증하기 전에 토큰을 먼저 확인하여, 유효한 토큰이 있다면 인증된 사용자로 바로 통과시키는 논리다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 비활성화 (JWT 를 사용하므로 불필요)
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 세션 방식 대신 JWT 방식을 사용하기 위한 무상태(Stateless) 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // API 경로별 접근 권한 설정
        http.authorizeHttpRequests((authorizeHttpRequest) ->
                authorizeHttpRequest
                        // Swagger UI 및 API 문서 접근은 인증 없이 허용
                        .requestMatchers("/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // 게시판의 전체 조회 및 단건 조회(GET 요청)는 인증 없이 허용
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        // 향후 구현할 회원가입, 로그인 API 경로는 인증 없이 허용 (미리 열어둠)
                        .requestMatchers("/users/signup", "/users/login").permitAll()
                        // 그 외의 모든 요청은 인증 처리가 필요함
                        .anyRequest().authenticated()
        );

        // JWT 필터를 기본 인증 필터 앞에 끼워 넣는다.
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
