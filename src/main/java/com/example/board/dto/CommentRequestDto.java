package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    @NotBlank(message = "댓글 내용은 필수 입력 값입니다.")
    private String content;
}
