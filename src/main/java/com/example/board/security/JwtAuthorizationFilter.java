package com.example.board.security;

import com.example.board.dto.ErrorResponseDto;
import com.example.board.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/*
 * @Slf4j : 로그 출력을 위한 롬복 어노테이션
 *
 * OncePerRequestFilter: 이 클래스를 상속받으면 클라이언트의 한 번 요청당 정확히 한 번만 이 필터가 동작하도록 보장한다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /*
     * doFilterInternal: 토큰을 추출하고 유효성을 검사한 뒤, 정상적인 토큰이면 setAuthentication 을 호출하여 인증 상태를 만든다.
     * 이 과정이 모두 끝나면 filterChain.doFilter 를 통해 다음 필터나 최종 목적지(Controller)로 요청을 통과시킨다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰 추출
        String tokenValue = jwtUtil.getJwtFromHeader(request);

        if (StringUtils.hasText(tokenValue)) {
            // 토큰 유효성 검사
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error, 유효하지 않은 토큰입니다.");
                jwtExceptionHandler(response, "토큰이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 토큰에서 사용자 정보 가져오기
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                // 인증 객체 생성 및 SecurityContext 에 담기
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error("인증 처리 중 예외 발생: {}", e.getMessage());
                jwtExceptionHandler(response, "인증 처리에 실패했습니다.", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    /*
     * 인증 처리
     *
     * setAuthentication / createAuthentication: 유효한 토큰의 사용자 아이디를 이용해 UserDetailsServiceImpl 에서 회원 정보를 가져온다.
     * 이후 스프링 시큐리티의 인증 객체인 UsernamePasswordAuthenticationToken 을 생성하고, 이를 전역 보안 저장소인 SecurityContextHolder 에 저장하여 현재 요청을 보낸 사용자가 '인증된 사용자'임을 시스템 전체에 알린다.
     */
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // 필터 전용 예외 처리 메서드
    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(new ErrorResponseDto(msg, statusCode));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
