package com.example.bulletinboardapp.dto.article.request;

import com.example.bulletinboardapp.entity.Article;
import com.example.bulletinboardapp.entity.User;
import lombok.Getter;

@Getter
public class ArticleCreateRequest {
    private String title;
    private String content;

    public Article toEntity(User user, String author) {
        return new Article(user, author, title, content);
    }
}
