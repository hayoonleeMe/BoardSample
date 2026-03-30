package com.example.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private String author;

    /*
     * @ManyToOne: 댓글(Many) 입장에서 게시글(One)과의 관계를 명시하는 어노테이션이다.
     * fetch = FetchType.LAZY: 지연 로딩 설정이다.
     * 댓글을 조회할 때 연관된 게시글 데이터가 즉시 필요하지 않다면 실제 사용 시점까지 조회를 미루어 성능을 최적화한다.
     *
     * @JoinColumn: 실제 데이터베이스 테이블에 생성될 외래 키 컬럼의 이름을 post_id 로 명시적으로 지정한다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Comment(String content, String author, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }

    public void update(String content) {
        this.content = content;
    }
}
