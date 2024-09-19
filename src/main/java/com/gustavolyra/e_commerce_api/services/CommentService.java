package com.gustavolyra.e_commerce_api.services;

import com.gustavolyra.e_commerce_api.dto.comments.CommentDto;
import com.gustavolyra.e_commerce_api.dto.comments.CommentDtoRequest;
import com.gustavolyra.e_commerce_api.entities.Comment;
import com.gustavolyra.e_commerce_api.repositories.CommentRepository;
import com.gustavolyra.e_commerce_api.repositories.ProductRepository;
import com.gustavolyra.e_commerce_api.services.exceptions.ForbiddenException;
import com.gustavolyra.e_commerce_api.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class CommentService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;

    public CommentService(UserService userService, ProductRepository productRepository, CommentRepository commentRepository) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
    }


    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public CommentDto addCommmentToProduct(UUID productId, CommentDtoRequest commentDtoRequest) {
        if (!productRepository.existsById(productId)) {
            log.error("Product with id {} not found", productId);
            throw new ResourceNotFoundException("Product not found");

        }
        var comment = createComment(commentDtoRequest, productId);
        comment = commentRepository.save(comment);
        return new CommentDto(comment);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public CommentDto replyComment(Long commentId, @Valid CommentDtoRequest commentDto) {
        var parentComment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with id {} not found", commentId);
            return new ResourceNotFoundException("Comment not found");
        });
        var comment = createComment(commentDto, parentComment.getProduct().getUuid());
        comment.setParentComment(parentComment);
        comment = commentRepository.save(comment);
        return new CommentDto(comment);
    }


    private Comment createComment(CommentDtoRequest commentDtoRequest, UUID productId) {
        Comment comment = new Comment();
        var user = userService.findUserFromAuthenticationContext();
        comment.setText(commentDtoRequest.text());
        comment.setAuthor(user);
        comment.setProduct(productRepository.getReferenceById(productId));
        comment.setMoment(Instant.now());
        return comment;
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteComment(Long commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        //verify if the user is the author of the comment
        var user = userService.findUserFromAuthenticationContext();
        if (!comment.getAuthor().getUsername().equals(user.getUsername())) {
            log.error("User {} is not the author of the comment", user.getUsername());
            throw new ForbiddenException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }

}