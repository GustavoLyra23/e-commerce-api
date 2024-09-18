package com.gustavolyra.e_commerce_api.dto.comments;

import com.gustavolyra.e_commerce_api.entities.Comment;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String author;
    private String text;
    private Instant moment;
    private Long replyId;
    private List<CommentDto> replies = new ArrayList<>();

    public CommentDto(Comment comment) {
        id = comment.getId();
        author = comment.getAuthor().getUsername();
        text = comment.getText();
        moment = comment.getMoment();
        if (comment.getParentComment() != null) {
            replyId = comment.getParentComment().getId();
        }

        if (comment.getReplies() != null) {
            comment.getReplies().forEach(reply -> replies.add(new CommentDto(reply)));
        }
    }


}
