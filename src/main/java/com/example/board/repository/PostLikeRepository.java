package com.example.board.repository;

import com.example.board.entity.Post;
import com.example.board.entity.PostLike;
import com.example.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 사용자와 게시글 정보를 바탕으로 기존 좋아요 내역을 찾는다.
    Optional<PostLike> findByUserAndPost(User user, Post post);
}
