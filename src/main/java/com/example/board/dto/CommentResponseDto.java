package com.example.board.dto;

import com.example.board.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
