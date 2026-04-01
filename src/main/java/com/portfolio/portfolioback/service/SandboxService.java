package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.common.enumtype.SandboxStatus;
import com.portfolio.portfolioback.dto.SandboxResponseDTO;

public interface SandboxService {
    String runCode() throws Exception;
    SandboxResponseDTO runCode(String code) throws Exception;
}
