package com.portfolio.portfolioback.common.exception;

/**
 * 다른 Exception과 구별하는 용도의 인터페이스
 */
public interface ErrorCodeProvider {
    ErrorCode getErrorCode();
}
