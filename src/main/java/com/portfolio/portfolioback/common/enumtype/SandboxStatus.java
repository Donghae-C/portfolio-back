package com.portfolio.portfolioback.common.enumtype;

public enum SandboxStatus {
    SUCCESS,        // 정상 실행 완료
    COMPILE_ERROR,  // 컴파일 에러
    RUNTIME_ERROR,  // 실행 중 예외 발생
    TIMEOUT,        // 시간 초과
    SYSTEM_ERROR    // 서버 내부 오류
}
