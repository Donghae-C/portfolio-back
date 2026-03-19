package com.portfolio.portfolioback.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyPortFolioException extends RuntimeException implements ErrorCodeProvider{
    private final ErrorCode errorCode;
    @Override
    public ErrorCode getErrorCode() {
        return null;
    }
}
