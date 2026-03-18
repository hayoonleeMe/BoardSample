package com.example.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * @Entity: 스프링(JPA)에게 이 클래스는 데이터베이스의 테이블과 매핑될 엔티티다라고 알려주는 표시다.
 * 이 클래스 이름대로 DB에 'post'라는 테이블이 생성된다.
 *
 * @Getter, @NoArgsConstructor: 롬복(Lombok)이 제공하는 편의 기능이다.
 * 캡슐화된 데이터를 안전하게 읽기 위한 getTitle() 같은 메서드들과, JPA가 내부적으로 필요로 하는 기본 생성자를 눈에 보이지 않게 자동으로 만들어 주어 코드를 간결하게 유지한다.
 */
@Entity
@Getter
@NoArgsConstructor
public class Post {

    /*
     * @Id: 이 테이블의 각 데이터를 구분하는 고유 식별자(Primary Key) 역할을 하는 필드임을 지정한다.
     * 게시판의 '글 번호'에 해당한다.
     *
     * @GeneratedValue(strategy = GenerationType.IDENTITY): 게시글이 새로 생성될 때마다 이 식별자 번호를 DB가 자동으로 1씩 증가시키도록 설정한다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * @Column: DB 테이블의 컬럼 속성을 세밀하게 지정한다.
     * 글자 길이를 제한하거나(length = 200), 반드시 값이 있어야 함(nullable = false)을 강제할 수 있다.
     * @Column을 생략한 author 필드도 기본 속성을 가진 컬럼으로 자동 인식된다.
     */
    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
