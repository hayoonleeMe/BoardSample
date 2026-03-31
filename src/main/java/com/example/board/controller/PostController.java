package com.example.board.controller;

import com.example.board.dto.PostRequestDto;
import com.example.board.dto.PostResponseDto;
import com.example.board.dto.PostUpdateRequestDto;
import com.example.board.security.UserDetailsImpl;
import com.example.board.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/*
 * @RestController: 이 클래스가 웹 요청을 처리하는 컨트롤러임을 스프링에 알린다.
 * 동시에 응답 결과로 완성된 웹 페이지(HTML)를 돌려주는 것이 아니라, 순수한 데이터(주로 JSON 형태)만을 반환하겠다고 선언하는 역할이다.
 *
 * @RequestMapping("/posts"): 이 컨트롤러가 담당할 기본 URL 주소를 지정한다.
 * 사용자가 인터넷 창에 웹서버주소/posts 라고 입력하거나 요청을 보내면 이 클래스가 작동하기 시작한다.
 *
 * @RequiredArgsConstructor: 롬복이 제공하는 기능이다.
 * 클래스 내부에 선언된 final 변수(여기서는 postService)를 스프링이 알아서 조립해 주는 의존성 주입(Dependency Injection)을 매우 간편하게 만들어 준다.
 * 이 덕분에 우리는 직접 레포지토리 객체를 생성하는 코드를 짜지 않아도 된다.
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /*
     * @PostMapping 과 @GetMapping: HTTP 통신 방식을 구분한다.
     * 새로운 데이터를 생성해 달라는 POST 요청이 오면 createPost 메서드가 실행되고, 데이터를 조회해 달라는 GET 요청이 오면 getAllPosts 메서드가 실행되어 앞서 만든 레포지토리의 기능을 호출하게 된다.
     *
     * @RequestBody: 사용자가 웹 브라우저에서 입력한 데이터를 서버로 보낼 때, 그 데이터가 담긴 HTTP 요청의 본문(Body)을 자바 객체로 변환해 주는 역할을 한다.
     * 프론트엔드에서 주로 JSON 형태의 텍스트로 데이터를 묶어서 보내면, 스프링이 이를 분석하여 우리가 앞서 만든 Post 클래스의 각 필드(title, content 등)에 알맞게 값을 짝지어 채워 넣어 준다.
     * 이 어노테이션을 명시하지 않으면 클라이언트가 보낸 데이터를 컨트롤러가 정상적으로 수신할 수 없다.
     *
     * @Valid: DTO 클래스 내부에 설정해둔 검증 규칙(@NotBlank, @Size 등)을 실제로 작동시키라고 스프링에게 내리는 실행 스위치다.
     * 클라이언트의 요청 데이터가 컨트롤러의 메서드 안으로 본격적으로 진입하기 직전에 문지기처럼 데이터를 먼저 검사한다.
     * 만약 규칙을 하나라도 통과하지 못하면 메서드 내부의 로직을 아예 실행하지 않고 즉시 예외(MethodArgumentNotValidException)를 발생시켜 불량 데이터의 침입을 원천 차단한다.
     *
     * @AuthenticationPrincipal : 스프링 시큐리티가 제공하는 어노테이션으로, 앞선 JwtAuthorizationFilter 에서 인증을 마치고 SecurityContext 에 담아두었던 인증 객체(UserDetailsImpl) 를 컨트롤러의 파라미터로 곧바로 꺼내어 주입해 준다.
     * 이를 통해 현재 요청을 보낸 사용자의 엔티티 정보를 서비스 계층으로 넘겨줄 수 있다.
     */
    @PostMapping
    public PostResponseDto createPost(@Valid @RequestBody PostRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestDto, userDetails.getUser());
    }

    /*
     * @RequestParam(required = false): 클라이언트가 URL 뒤에 ?keyword=단어 형태로 데이터를 보낼 때, 이를 자바의 keyword 변수로 매핑해 주는 어노테이션이다.
     * required = false 속성을 주면 검색어를 보내지 않아도 에러가 발생하지 않고 자바 변수에 null이 들어온다.
     *
     * Pageable pageable: 클라이언트가 URL 뒤에 ?page=0&size=10 처럼 조건을 달아서 보내면, 스프링이 이를 인식하여 Pageable 객체로 자동 변환해 준다. (주의: 스프링의 페이지 번호는 0부터 시작한다.)
     *
     * @PageableDefault: 만약 클라이언트가 아무 조건 없이 /posts 라고만 요청했을 때 적용될 기본값을 설정하는 방어벽이다.
     * 위 코드에서는 "기본적으로 한 페이지당 10개씩(size = 10), 글 번호(id)를 기준으로 최신 글이 위로 오도록 내림차순(DESC) 정렬해서 보여줘"라는 의미를 담고 있다.
     */
    @GetMapping
    public Page<PostResponseDto> getAllPosts(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // 문자열 검증
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색어가 존재하면 찾아서 조회
            return postService.searchPosts(keyword, pageable);
        }
        // 검색어가 존재하지 않으면 전체 조회
        return postService.getAllPosts(pageable);
    }

    /*
     * @PathVariable: 앞서 RESTful API에서는 자원을 명시할 때 /posts/1 처럼 주소 뒤에 식별자를 붙인다고 설명했다.
     * 주소창에 들어온 이 1이라는 숫자를 자바 코드의 id 변수로 쏙 뽑아내어 연결해 주는 어노테이션이다.
     */
    @GetMapping("/{id}")
    public PostResponseDto getPost(
            @PathVariable Long id,
            HttpServletRequest request,
            HttpServletResponse response) {
        return postService.getPost(id, request, response);
    }

    /*
     * @PutMapping / @DeleteMapping: 자원의 수정과 삭제를 의미하는 HTTP 메서드 규칙에 맞춰 컨트롤러의 동작을 분리했다.
     */
    @PutMapping("/{id}")
    public PostResponseDto updatePost(@PathVariable Long id,
                                      @Valid @RequestBody PostUpdateRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(id, requestDto, userDetails.getUser());
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(id, userDetails.getUser());
    }

    // 좋아요 토글 API
    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String message = postService.toggleLike(id, userDetails.getUser());
        return ResponseEntity.ok(message);
    }
}