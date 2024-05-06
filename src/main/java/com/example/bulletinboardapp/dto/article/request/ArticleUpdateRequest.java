package com.example.bulletinboardapp.dto.article.request;

import lombok.Getter;

@Getter
public class ArticleUpdateRequest {
    private Long id;
    private String title;
    private String content;
}
