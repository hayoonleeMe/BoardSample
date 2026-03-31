package com.example.board.repository;

import com.example.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * JpaRepository 상속: JpaRepository<Post, Long>을 상속받는 것만으로 데이터베이스와 통신할 준비가 끝난다.
 * 꺾쇠 안의 첫 번째 자리(Post)는 이 창구가 다룰 대상 엔티티를 의미하고, 두 번째 자리(Long)는 그 엔티티의 고유 식별자(@Id) 자료형을 의미한다.
 *
 * 자동 구현의 마법: 개발자가 이렇게 껍데기(인터페이스)만 선언해 두면, 스프링이 백엔드 서버를 실행할 때 내부적으로 데이터베이스 연결과 쿼리 전송에 필요한 복잡한 실제 코드를 알아서 작성해 메모리에 올려준다.
 *
 * 기본 기능 즉시 사용: 결과적으로 우리는 아무 코드도 짜지 않았지만, 이 레포지토리를 통해 데이터를 저장하는 save(), 모든 게시글을 가져오는 findAll(), 글 번호로 하나만 찾는 findById() 등의 기능을 즉시 호출해서 쓸 수 있게 된다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /*
     * 스프링 데이터 JPA의 가장 강력한 특징은 자바 인터페이스에 규칙에 맞는 이름으로 추상 메서드만 선언해 두면, 구현체 클래스를 작성하지 않아도 스프링이 알아서 데이터베이스 조회 코드를 만들어준다는 것이다.
     * 이를 쿼리 메서드(Query Method)라고 부른다.
     *
     * 함수 이름의 문법적 의미
     * findBy: 데이터를 조회하겠다는 기본 명령어다.
     * Title: 검색의 기준이 되는 자바 엔티티(Post)의 필드 이름이다.
     * Containing: 전달받은 문자열(keyword)이 필드 값 앞, 뒤, 중간 어디든 포함되어 있으면 찾아오라는 의미다. (SQL의 LIKE %keyword% 와 동일하게 동작한다.)
     */
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user", countQuery = "SELECT count(p) FROM Post p")
    Page<Post> findAllWithUser(Pageable pageable);
}
