package com.example.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
public class Post extends BaseTimeEntity {

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

    /*
     * @OneToMany: 게시글(One) 입장에서 댓글(Many)과의 관계를 명시한다.
     *
     * mappedBy: 연관관계의 주인이 자신이 아니라 Comment 엔티티에 있는 post 필드임을 선언한다.
     * 이 속성을 적어주어야 현재 필드가 읽기 전용으로 동작하며, 불필요한 외래 키 업데이트 쿼리가 발생하는 것을 방지한다.
     *
     * cascade = CascadeType.ALL: 부모 엔티티(게시글)의 상태 변화를 자식 엔티티(댓글)에도 전파한다.
     * 즉, 게시글을 삭제하면 데이터베이스에 남은 연관 댓글들도 자동으로 함께 삭제된다.
     *
     * orphanRemoval = true: 부모와의 연관관계가 끊어진 자식 엔티티(고아 객체)를 데이터베이스에서 자동으로 삭제해 주는 옵션이다.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

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
