package com.example.board.service;

import com.example.board.dto.CommentRequestDto;
import com.example.board.dto.CommentResponseDto;
import com.example.board.dto.CommentUpdateRequestDto;
import com.example.board.entity.Comment;
import com.example.board.entity.Post;
import com.example.board.entity.User;
import com.example.board.entity.UserRoleEnum;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + postId));

        Comment comment = new Comment(requestDto.getContent(), post, user);
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentUpdateRequestDto requestDto, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id=" + commentId));
        checkUserAuthorization(comment, user);

        comment.update(requestDto.getContent());
        return new CommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id=" + commentId));
        checkUserAuthorization(comment, user);

        commentRepository.delete(comment);
    }

    // 작성자 일치 여부를 검증하는 공통 메서드
    private void checkUserAuthorization(Comment comment, User user) {
        // 사용자의 권한이 관리자(ADMIN)가 아니고, 댓글의 작성자 아이디와 현재 로그인한 사용자의 아이디가 다를 경우 예외 발생
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자 또는 관리자만 수정 및 삭제할 수 있습니다.");
        }
    }
}
