package com.example.board.controller;

import com.example.board.dto.CommentRequestDto;
import com.example.board.dto.CommentResponseDto;
import com.example.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/*
 * @RequestMapping("/posts/{postId}/comments"): 이 컨트롤러가 처리할 기본 URL 주소를 지정한다.
 * 중괄호 {} 로 감싸진 postId 는 클라이언트가 요청할 때마다 동적으로 변하는 값(예: 1번 글, 2번 글)을 의미한다.
 */
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /*
     * @PathVariable: URL 경로에 포함된 동적인 변수({postId})의 값을 추출하여 자바 메서드의 매개변수인 Long postId 에 매핑해 주는 역할을 한다.
     */
    @PostMapping
    public CommentResponseDto createComment(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto requestDto) {
        return commentService.createComment(postId, requestDto);
    }
}
