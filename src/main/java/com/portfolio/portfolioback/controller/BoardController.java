package com.portfolio.portfolioback.controller;

import com.portfolio.portfolioback.common.security.CustomUserDetails;
import com.portfolio.portfolioback.common.util.S3Uploader;
import com.portfolio.portfolioback.dto.BoardInboundDTO;
import com.portfolio.portfolioback.dto.BoardOutboundDTO;
import com.portfolio.portfolioback.dto.ReplyInboundDTO;
import com.portfolio.portfolioback.dto.ReplyOutboundDTO;
import com.portfolio.portfolioback.service.BoardService;
import com.portfolio.portfolioback.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Slf4j
public class BoardController {
    private final BoardService boardService;
    private final ReplyService replyService;
    private final S3Uploader s3Uploader;

    @GetMapping
    public ResponseEntity<?> getBoard(@RequestParam int page, @RequestParam int size){
        log.info("Get Board");
        log.info("Page: {}, Size: {}", page, size);
        Page<BoardOutboundDTO> allBoards = boardService.findAllBoards(page, size);
        log.info("All Boards: {}", allBoards.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(allBoards);
    }

    @PostMapping
    public ResponseEntity<?> writeBoard(@RequestBody BoardInboundDTO boardInboundDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("Board Write");
        boardInboundDTO.setUserId(customUserDetails.getUser().getUserId());
        log.info("BoardDTO = {}",  boardInboundDTO);
        boardService.writeBoard(boardInboundDTO);
        return ResponseEntity.status(HttpStatus.OK).body("작성 완료");
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<?> getBoardById(@PathVariable Long boardId){
        log.info("Get Board by Id, {}", boardId);
        BoardOutboundDTO board = boardService.findBoardById(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<?> updateBoard(@PathVariable Long boardId,
                                         @RequestBody BoardInboundDTO boardInboundDTO,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("Update Board");
        Long userId = customUserDetails.getUser().getUserId();
        boardInboundDTO.setUserId(userId);
        boardService.updateBoard(boardInboundDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("Delete Board");
        Long userId = customUserDetails.getUser().getUserId();
        BoardInboundDTO board = BoardInboundDTO.builder().boardId(boardId).userId(userId).build();
        boardService.deleteBoard(board);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/reply/{boardId}")
    public ResponseEntity<?> getBoardReply(@PathVariable Long boardId){
        log.info("Get Board Reply");
        List<ReplyOutboundDTO> replyList = replyService.getReplyList(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(replyList);
    }

    @PostMapping("/reply")
    public ResponseEntity<?> writeBoardReply(@RequestBody ReplyInboundDTO replyInboundDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("Write Board Reply");
        replyInboundDTO.setUserId(customUserDetails.getUser().getUserId());
        log.info("ReplyInboundDTO = {}",  replyInboundDTO);
        replyService.writeReply(replyInboundDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<?> deleteBoardReply(@PathVariable Long replyId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("Delete Board Reply");
        Long userId = customUserDetails.getUser().getUserId();
        ReplyInboundDTO replyInboundDTO = ReplyInboundDTO.builder().replyId(replyId).userId(userId).build();
        replyService.deleteReply(replyInboundDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Upload Image");
        String url = s3Uploader.uploadFile(file, "image");
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("url", url));
    }
}
