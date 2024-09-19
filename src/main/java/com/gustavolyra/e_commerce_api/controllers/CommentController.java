package com.gustavolyra.e_commerce_api.controllers;

import com.gustavolyra.e_commerce_api.dto.comments.CommentDto;
import com.gustavolyra.e_commerce_api.dto.comments.CommentDtoRequest;
import com.gustavolyra.e_commerce_api.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public ResponseEntity<CommentDto> createComment(@PathVariable("id") UUID productId, @Valid @RequestBody CommentDtoRequest commentDto) {
        var comment = commentService.addCommmentToProduct(productId, commentDto);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public ResponseEntity<CommentDto> replyComment(@PathVariable("id") Long commentId, @Valid @RequestBody CommentDtoRequest commentDto) {
        var comment = commentService.replyComment(commentId, commentDto);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}