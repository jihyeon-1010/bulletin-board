package com.example.bulletinboardapp.dto.comment.request;

import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private String body;
    private long articleId;
}
