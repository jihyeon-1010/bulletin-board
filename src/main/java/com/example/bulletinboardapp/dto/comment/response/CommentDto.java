package com.example.bulletinboardapp.dto.comment.response;

import com.example.bulletinboardapp.entity.Comment;
import com.example.bulletinboardapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentDto {
    private Long id;
    private Long articleId;
    private User user;
    private String author;
    private String body;
    private boolean editable;

    public static CommentDto createCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getUser(),
                comment.getAuthor(),
                comment.getBody(),
                false
        );
    }

    public void isEditable() {
        this.editable = true;
    }
}
