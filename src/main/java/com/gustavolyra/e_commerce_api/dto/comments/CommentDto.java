package com.gustavolyra.e_commerce_api.dto.comments;

import com.gustavolyra.e_commerce_api.entities.Comment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CommentDto {

    private Long id;
    private String author;
    private String text;
    private Instant moment;
    private Long replyId;
    List<CommentDto> replies = new ArrayList<>();

    public CommentDto(Comment comment) {
        id = comment.getId();
        author = comment.getAuthor().getUsername();
        text = comment.getText();
        moment = comment.getMoment();
        replyId = comment.getParentComment().getId();
        if (comment.getReplies() != null) {
            comment.getReplies().forEach(reply -> replies.add(new CommentDto(reply)));
        }
    }


}
