package com.example.board.repository;

import com.example.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * JpaRepository 상속: JpaRepository<Post, Long>을 상속받는 것만으로 데이터베이스와 통신할 준비가 끝난다.
 * 꺾쇠 안의 첫 번째 자리(Post)는 이 창구가 다룰 대상 엔티티를 의미하고, 두 번째 자리(Long)는 그 엔티티의 고유 식별자(@Id) 자료형을 의미한다.
 *
 * 자동 구현의 마법: 개발자가 이렇게 껍데기(인터페이스)만 선언해 두면, 스프링이 백엔드 서버를 실행할 때 내부적으로 데이터베이스 연결과 쿼리 전송에 필요한 복잡한 실제 코드를 알아서 작성해 메모리에 올려준다.
 *
 * 기본 기능 즉시 사용: 결과적으로 우리는 아무 코드도 짜지 않았지만, 이 레포지토리를 통해 데이터를 저장하는 save(), 모든 게시글을 가져오는 findAll(), 글 번호로 하나만 찾는 findById() 등의 기능을 즉시 호출해서 쓸 수 있게 된다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
