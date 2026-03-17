package com.example.board.service;

import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;
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
}
