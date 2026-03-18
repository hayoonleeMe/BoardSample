package com.example.board.dto;

import lombok.Getter;

@Getter
public class ErrorResponseDto {
    
    private String message;
    private int statusCode;

    public ErrorResponseDto(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
