package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequestDto {

    /*
     * @NotBlank: 값이 null이거나, 텅 비어있거나(""), 공백만 있는(" ") 경우를 모두 강력하게 차단한다.
     * @Size: 문자열의 길이를 제한한다. 데이터베이스의 용량을 보호하기 위한 최소한의 조치다.
     * message: 규칙을 어겼을 때 스프링이 생성할 기본 에러 메시지를 우리가 원하는 한글로 덮어씌운다.
     */
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    @NotBlank(message = "작성자는 필수 입력 값입니다.")
    private String author;
}
