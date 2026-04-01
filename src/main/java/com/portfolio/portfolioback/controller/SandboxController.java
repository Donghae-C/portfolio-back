package com.portfolio.portfolioback.controller;

import com.portfolio.portfolioback.dto.CodeRequest;
import com.portfolio.portfolioback.dto.SandboxResponseDTO;
import com.portfolio.portfolioback.service.SandboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/sandbox")
public class SandboxController {

    private final SandboxService sandboxService;

    @GetMapping
    public ResponseEntity<?> sandbox() throws Exception {
        String result = sandboxService.runCode();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", result));
    }

    @PostMapping
    public SandboxResponseDTO sandbox(@RequestBody CodeRequest codeRequest) throws Exception {
        log.info("sandbox code: {}", codeRequest.getCode());
        return sandboxService.runCode(codeRequest.getCode());
    }
}
