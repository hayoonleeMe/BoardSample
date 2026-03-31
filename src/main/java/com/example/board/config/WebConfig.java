package com.example.board.config;

import com.example.board.util.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * @Configuration: 이 클래스가 스프링 컨테이너의 전반적인 환경 설정을 담당하는 자바 클래스임을 선언한다.
 * 애플리케이션이 시작될 때 스프링이 가장 먼저 이 클래스를 읽고 설정에 반영한다.
 *
 * implements WebMvcConfigurer: 자바의 인터페이스 상속(구현) 문법이다.
 * 스프링이 웹에 관련된 기본 설정들을 모아둔 WebMvcConfigurer 인터페이스를 상속받아, 우리가 필요한 기능(CORS 설정)만 선택해서 자바 코드로 덮어씌울 수 있게 해준다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
     * @Override: 부모 인터페이스에 정의된 addCorsMappings 메서드를 자식 클래스에서 재정의(Overriding)하여 사용한다는 것을 자바 컴파일러에게 명시적으로 알리는 어노테이션이다.
     *
     * 메서드 체이닝(Method Chaining): registry.addMapping().allowedOrigins()... 처럼 객체의 메서드가 자기 자신을 반환하도록 설계하여, 마침표(.)를 사용해 연속적으로 설정을 엮어나가는 자바의 흔한 코딩 기법이다.
     * /**는 /posts를 포함한 모든 하위 API 주소에 이 규칙을 적용하겠다는 의미다.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 허용할 프론트엔드 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                // 프론트엔드 자바스크립트에서 Authorization 헤더를 읽을 수 있도록 노출시킨다.
                .exposedHeaders(JwtUtil.AUTHORIZATION_HEADER)
                .maxAge(3600);
    }
}
