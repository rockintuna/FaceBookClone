package com.spring.clone.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PASSWORD_EQUAL(BAD_REQUEST,"비밀번호가 일치하지않습니다"),

    POST_NOT_FOUND(BAD_REQUEST, "해당 게시글을 찾을 수 없습니다."),

    EMAIL_DUPLICATE(BAD_REQUEST, "중복된 아이디가 존재합니다."),
    USER_NOT_FOUND(BAD_REQUEST, "회원 정보를 찾을 수 없습니다."),

    PASSWORD_PATTERN_LENGTH(BAD_REQUEST, "비밀번호는 6~20자리로 해주세요"),
    PASSWORD_ENTER(BAD_REQUEST, "비밀번호를 입력해주세요"),
    ADMIN_PASSWORD_DISCORDANCE(BAD_REQUEST, "관리자 암호가 틀려 등록이 불가능합니다."),

    NICKNAME_DUPLICATE(BAD_REQUEST, "중복된 닉네임이 존재합니다."),

    LOGIN_TOKEN_EXPIRE(BAD_REQUEST, "로그인이 만료되었습니다. 재로그인 하세요!");

    private final HttpStatus httpStatus;
    private final String message;
} //이거를 다 예외로 exception 클래스를 한줄 하나하나의 클래스로