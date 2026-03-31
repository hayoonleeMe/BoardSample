package com.example.board.repository;

import com.example.board.entity.Comment;
import com.example.board.entity.CommentLike;
import com.example.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
}
