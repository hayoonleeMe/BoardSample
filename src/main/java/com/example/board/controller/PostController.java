package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     */
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }
}