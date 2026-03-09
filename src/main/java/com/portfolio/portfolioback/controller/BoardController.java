package com.portfolio.portfolioback.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    @PostMapping("/board/write")
    public ResponseEntity<?> writeBoard(){
        log.info("Board Write");
        return ResponseEntity.status(HttpStatus.OK).body("작성 완료");
    }
}
