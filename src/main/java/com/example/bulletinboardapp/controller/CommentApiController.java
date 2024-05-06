package com.example.bulletinboardapp.controller;

import com.example.bulletinboardapp.dto.comment.request.CommentUpdateRequest;
import com.example.bulletinboardapp.dto.comment.response.CommentDto;
import com.example.bulletinboardapp.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentApiController {
    private final CommentService commentService;

    // 댓글 조회
    @GetMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable long articleId, Principal principal) {
        List<CommentDto> dtos = commentService.comments(articleId);

        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    // 댓글 생성
    @PostMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable long articleId, @RequestBody CommentDto dto, Principal principal) {
        CommentDto createdDto = commentService.create(articleId, dto, principal);

        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }

    // 댓글 수정
    @PutMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable long id, @RequestBody CommentUpdateRequest request) {
        CommentDto updatedDto = commentService.update(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> delete(@PathVariable long id) {
        CommentDto deletedDto = commentService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body(deletedDto);
    }
}
