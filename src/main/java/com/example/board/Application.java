package com.example.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
 * @SpringBootApplication: 이 어노테이션이 붙어있는 클래스를 스프링 부트는 전체 프로젝트의 심장이자 본부로 인식한다.
 * 이 표시 하나로 인해 스프링은 우리가 앞서 추가한 웹, 데이터베이스 관련 라이브러리 설정들을 자동으로 스캔하고 엮어서 내장 톰캣 웹 서버를 구동할 준비를 마친다.
 *
 * @EnableJpaAuditing: JPA Auditing 활성화
 */
@EnableJpaAuditing
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		/*
		 * main 메서드와 SpringApplication.run(): 자바 언어의 특성상 프로그램이 실행될 때 가장 먼저 찾는 곳이 main 메서드다.
		 * 이 안에서 SpringApplication.run(클래스명, args) 명령을 호출하는데, 이 단 한 줄의 코드가 앞서 말한 모든 자동 설정들을 작동시키고 실제 백엔드 서버를 켜는 핵심 스위치 역할을 한다.
		 */
		SpringApplication.run(Application.class, args);
	}
}
