package com.portfolio.portfolioback.controller;

import com.portfolio.portfolioback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("api/me")
    public ResponseEntity<?> getMe() {
        return ResponseEntity.status(HttpStatus.OK).body("일단 여기까지만");
    }
}
