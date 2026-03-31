package com.example.board.util;

import com.example.board.entity.UserRoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/*
 * @Component : 스프링 프레임워크가 시작될 때 이 클래스를 찾아 스스로 관리하는 객체(빈, Bean)로 등록하도록 지시하는 어노테이션이다.
 * 이를 통해 UserService 같은 다른 서비스 객체에서 필요할 때마다 new JwtUtil() 로 생성하지 않고, 스프링으로부터 자동으로 주입받아 사용할 수 있게 된다.
 */
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
}
