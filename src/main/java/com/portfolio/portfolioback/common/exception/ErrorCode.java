package com.portfolio.portfolioback.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 필요한 에러코드 추가할 것..
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BOARD_NOTFOUND(HttpStatus.BAD_REQUEST, "invalid board", "게시물 없음"),
    BOARD_DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "db error", "db에 문제가 있을수도"),
    USER_NOTFOUND(HttpStatus.BAD_REQUEST, "not found user", "그런 유저 없음"),
    NOT_AUTH(HttpStatus.BAD_REQUEST, "not auth", "권한 없음"),
    REPLY_NOTFOUND(HttpStatus.BAD_REQUEST, "invalid reply", "댓글 없음"),
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "db error", "db에 문제가 있을수도");

    private final HttpStatus httpStatus;
    private final String title;
    private final String message;
}
