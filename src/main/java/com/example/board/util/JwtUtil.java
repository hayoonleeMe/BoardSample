package com.example.board.util;

import com.example.board.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/*
 * @Slf4j : 로그 출력을 위한 롬복 어노테이션
 *
 * @Component : 스프링 프레임워크가 시작될 때 이 클래스를 찾아 스스로 관리하는 객체(빈, Bean)로 등록하도록 지시하는 어노테이션이다.
 * 이를 통해 UserService 같은 다른 서비스 객체에서 필요할 때마다 new JwtUtil() 로 생성하지 않고, 스프링으로부터 자동으로 주입받아 사용할 수 있게 된다.
 */
@Slf4j
@Component
public class JwtUtil {

    // HTTP 응답 헤더에 토큰을 담을 때 사용할 키 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 토큰의 식별자 역할을 하는 접두사 (끝에 공백 포함)
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료 시간 (60분)
    private final long TOKEN_TIME = 60 * 60 * 1000L;

    /*
     * @Value("${jwt.secret.key}") : application.yml 파일에 설정해 둔 속성값을 자바 코드의 변수로 곧바로 가져올 때 사용한다.
     * 설정 파일과 외부 환경변수를 거쳐 들어온 안전한 비밀키 문자열이 secretKey 변수에 담기게 된다.
     */
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    /*
     * 스프링 빈(Bean)이 생성된 후 비밀키를 Base64 로 디코딩하여 Key 객체로 변환한다.
     *
     * @PostConstruct : 객체가 생성되고 @Value 를 통한 값 주입까지 모두 완료된 직후에, 해당 메서드(init())를 딱 한 번만 자동 실행하도록 보장한다.
     * 자바의 일반 문자열 형태인 비밀키를 실제 해싱 알고리즘에 사용할 수 있는 암호화 전용 객체(Key 타입)로 변환하는 준비 작업을 여기서 수행한다.
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /*
     * 사용자 이름과 권한 정보를 바탕으로 새로운 JWT 를 생성한다.
     *
     * Jwts.builder() : 외부 라이브러리인 JJWT 가 제공하는 기능으로, 토큰의 구조를 순차적으로 조립해 나간다.
     * setSubject 에 사용자의 고유 아이디를 담고, claim 에 권한 등 추가 정보를 넣으며, setExpiration 으로 만료 시간을 지정한다.
     * 마지막으로 signWith 를 호출해 앞서 만든 비밀키 객체로 위조 방지용 서명을 찍어 최종적인 JWT 문자열을 발행한다.
     */
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim("auth", role.getAuthority()) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    /*
     * getJwtFromHeader: 클라이언트의 요청 정보를 담고 있는 HttpServletRequest 에서 Authorization 헤더 값을 꺼낸다.
     * 값이 존재하고 Bearer  로 시작한다면, 접두사 7글자를 잘라내어 순수한 토큰 문자열만 반환한다.
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /*
     * validateToken: 주입받은 비밀키(key)를 사용해 토큰을 해독해 본다.
     * 이 과정에서 토큰이 만료되었거나 누군가 임의로 변조했다면 예외(Exception)가 발생하여 false 를 반환하게 된다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            // 위조되었거나 손상된 토큰
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            log.error("Expired JWT token, 만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 형식의 토큰
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            // 빈 토큰
            log.error("JWT claims is empty, 잘못된 JWT 토큰입니다.");
        }
        return false;
    }

    /*
     * getUserInfoFromToken: 유효성이 검증된 토큰의 본문(Body)을 열어 그 안에 담아두었던 사용자 아이디(Subject)와 권한(Claims) 정보를 꺼내어 반환한다.
     */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
