package com.example.board.service;

import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @Service: 스프링에게 이 클래스가 비즈니스 로직을 담당하는 서비스 계층임을 알리고, 스프링이 객체를 관리하도록 만든다.
 *
 * 의존성 주입: 앞서 컨트롤러에서 했던 것과 마찬가지로 @RequiredArgsConstructor를 통해 PostRepository를 주입받아 데이터베이스에 접근한다.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post createPost(Post post) {
        // 나중에 여기에 데이터를 검증하거나 가공하는 비즈니스 로직을 추가할 수 있다.
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    /*
     * findById와 orElseThrow: 데이터베이스에서 id(글 번호)를 기준으로 데이터를 찾는다.
     * 만약 누군가 이미 삭제했거나 없는 번호를 요청했을 경우, 프로그램이 멈추지 않도록 예외(에러 메시지)를 발생시키는 안전장치다.
     */
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));
    }

    /*
     * @Transactional: 수정과 삭제 로직에 붙은 이 어노테이션은 해당 메서드가 실행되는 전체 과정을 하나의 작업 단위(트랜잭션)로 묶어준다.
     * 특히 수정을 할 때 이 어노테이션이 있어야만 앞서 설명한 변경 감지(Dirty Checking) 마법이 발동하여 데이터베이스에 값이 정상적으로 반영된다.
     */
    @Transactional
    public Post updatePost(Long id, Post requestPost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));
        post.update(requestPost.getTitle(), requestPost.getContent());
        return post;
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));
        postRepository.delete(post);
    }
}
