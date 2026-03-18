package com.example.board.exception;

import com.example.board.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * @RestControllerAdvice: 프로젝트 내에 존재하는 모든 컨트롤러(@RestController)를 실시간으로 감시하는 전역 보호망 역할을 한다.
 * 개별 컨트롤러마다 에러 처리 코드를 중복해서 작성할 필요 없이, 서버 어디서 에러가 터지든 이 클래스가 즉시 낚아채어 한 곳에서 일괄 처리하게 해준다.
 * 또한, 처리 결과를 복잡한 에러 웹페이지가 아닌 깔끔한 JSON 데이터 형태로 클라이언트에게 반환하도록 보장한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * @ExceptionHandler: 괄호 안에 지정한 특정 예외(여기서는 IllegalArgumentException)가 발생하면 이 메서드가 실행되도록 연결해 준다.
     *
     * ResponseEntity: 클라이언트에게 응답을 보낼 때 데이터(바디)뿐만 아니라 HTTP 상태 코드(예: 400, 404, 500 등)까지 개발자가 정밀하게 조작해서 보낼 수 있게 해주는 스프링의 기본 도구다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        // 예외 메시지와 400 Bad Request status code
        ErrorResponseDto responseDto = new ErrorResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value());

        // ResponseEntity라는 스프링의 웹 응답용 포장지에 한 번 더 감싸서 반환한다.
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        ErrorResponseDto responseDto = new ErrorResponseDto(errorMessage, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}
