package com.example.board.service;

import com.example.board.dto.PostRequestDto;
import com.example.board.dto.PostResponseDto;
import com.example.board.dto.PostUpdateRequestDto;
import com.example.board.entity.Post;
import com.example.board.entity.PostLike;
import com.example.board.entity.User;
import com.example.board.entity.UserRoleEnum;
import com.example.board.repository.PostLikeRepository;
import com.example.board.repository.PostRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/*
 * @Service: 스프링에게 이 클래스가 비즈니스 로직을 담당하는 서비스 계층임을 알리고, 스프링이 객체를 관리하도록 만든다.
 *
 * 의존성 주입: 앞서 컨트롤러에서 했던 것과 마찬가지로 @RequiredArgsConstructor를 통해 PostRepository를 주입받아 데이터베이스에 접근한다.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    private static final String VIEW_COOKIE_PREFIX = "postView_";
    private static final int VIEW_COOKIE_MAX_AGE = 60 * 60 * 24; // 24시간

    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
        Post post = new Post(requestDto.getTitle(), requestDto.getContent(), user);
        Post savedPost = postRepository.save(post);
        return new PostResponseDto(savedPost);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        // Repository에 Pageable 객체를 넘기면 알아서 데이터베이스에서 해당 페이지 번호의 조각만 가져온다.
        Page<Post> postPage = postRepository.findAllWithUser(pageable);

        // 찾아온 Page<Post> 객체를 map 함수를 이용해 Page<PostResponseDto>로 변환하여 반환한다.
        return postPage.map(PostResponseDto::new);
    }

    /*
     * findById와 orElseThrow: 데이터베이스에서 id(글 번호)를 기준으로 데이터를 찾는다.
     * 만약 누군가 이미 삭제했거나 없는 번호를 요청했을 경우, 프로그램이 멈추지 않도록 예외(에러 메시지)를 발생시키는 안전장치다.
     */
    @Transactional
    public PostResponseDto getPost(Long id, HttpServletRequest request, HttpServletResponse response) {
        Post post = findPost(id);

        // 조회수 중복 방지 로직 (Cookie 활용)
        Cookie[] cookies = request.getCookies();
        boolean isVisited = false;

        String targetCookieName = VIEW_COOKIE_PREFIX + id;

        // 클라이언트가 보낸 쿠키들을 순회하며 해당 게시글 번호가 적힌 쿠키가 있는지 확인한다.
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(targetCookieName)) {
                    isVisited = true;
                    break;
                }
            }
        }

        if (!isVisited) {
            post.incrementViewCount();

            // 조합된 쿠키 이름과 상수로 정의된 만료 시간을 적용한다.
            Cookie newCookie = new Cookie(targetCookieName, "true");
            newCookie.setMaxAge(VIEW_COOKIE_MAX_AGE);
            newCookie.setPath("/");

            response.addCookie(newCookie);
        }

        return new PostResponseDto(post);
    }

    /*
     * @Transactional: 수정과 삭제 로직에 붙은 이 어노테이션은 해당 메서드가 실행되는 전체 과정을 하나의 작업 단위(트랜잭션)로 묶어준다.
     * 특히 수정을 할 때 이 어노테이션이 있어야만 앞서 설명한 변경 감지(Dirty Checking) 마법이 발동하여 데이터베이스에 값이 정상적으로 반영된다.
     */
    @Transactional
    public PostResponseDto updatePost(Long id, PostUpdateRequestDto requestDto, User user) {
        Post post = findPost(id);
        checkUserAuthorization(post, user);

        post.update(requestDto.getTitle(), requestDto.getContent());
        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long id, User user) {
        Post post = findPost(id);
        checkUserAuthorization(post, user);

        postRepository.delete(post);
    }

    public Page<PostResponseDto> searchPosts(String keyword, Pageable pageable) {
        Page<Post> postPage = postRepository.findByTitleContaining(keyword, pageable);
        return postPage.map(PostResponseDto::new);
    }

    @Transactional
    public String toggleLike(Long postId, User user) {
        Post post = findPost(postId);

        // 사용자가 해당 게시글에 이미 좋아요를 눌렀는지 확인한다.
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);

        if (postLike.isPresent()) {
            // 이미 존재한다면 좋아요 취소로 간주하고 삭제한다.
            postLikeRepository.delete(postLike.get());
            post.decrementLikeCount();
            return "좋아요 취소 완료";
        } else {
            // 존재하지 않는다면 좋아요 추가로 간주하고 저장한다.
            postLikeRepository.save(new PostLike(user, post));
            post.incrementLikeCount();
            return "좋아요 완료";
        }
    }

    private Post findPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));
    }

    // 작성자 일치 여부를 검증하는 공통 메서드
    private void checkUserAuthorization(Post post, User user) {
        // 사용자의 권한이 관리자(ADMIN)가 아니고, 게시글의 작성자 아이디와 현재 로그인한 사용자의 아이디가 다를 경우 예외 발생
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자 또는 관리자만 수정 및 삭제할 수 있습니다.");
        }
    }
}
