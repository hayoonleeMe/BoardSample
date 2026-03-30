package com.example.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * @Table(name = "users"): 데이터베이스에 생성될 테이블의 이름을 명시적으로 지정한다.
 * 관계형 데이터베이스에서 user 라는 단어는 시스템 예약어로 쓰이는 경우가 많아 그대로 테이블 이름으로 사용하면 오류가 발생할 수 있다.
 * 따라서 users 와 같이 복수형으로 지정하는 것이 안전하다.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 로그인 아이디 용도 (중복 불가)
     *
     * @Column(nullable = false, unique = true): nullable = false 는 데이터베이스 컬럼에 빈 값(NULL)이 들어갈 수 없음을 의미하고, unique = true 는 중복된 값을 허용하지 않는 고유 제약조건을 설정한다.
     * 이를 통해 동일한 로그인 아이디(username)로 여러 번 가입하는 것을 원천적으로 차단한다.
     */
    @Column(nullable = false, unique = true)
    private String username;

    // 암호화되어 저장될 비밀번호
    @Column(nullable = false)
    private String password;

    /*
     * @Enumerated(value = EnumType.STRING): 자바의 Enum 타입 데이터를 데이터베이스에 어떤 형태로 저장할지 결정하는 어노테이션이다.
     * 이 설정을 생략하면 기본적으로 선언된 순서에 따라 숫자(0, 1 등)로 저장된다.
     * 하지만 나중에 Enum 의 코드가 수정되어 항목이 추가되거나 순서가 바뀌면 데이터베이스 안의 의미가 완전히 뒤섞이는 치명적인 버그가 발생한다.
     * 따라서 실무에서는 반드시 EnumType.STRING 을 적용하여 "USER", "ADMIN" 같은 문자열 자체를 데이터베이스에 저장하도록 해야 한다.
     */
    // 사용자 권한 (USER, ADMIN)
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(String username, String password, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
