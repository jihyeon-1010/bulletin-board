package com.example.bulletinboardapp.repository;

import com.example.bulletinboardapp.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 게시글의 제목과 내용 중에서 쿼리의 문자열이 포함되어 있는 게시글 가져오기
    List<Article> findByTitleContainingOrContentContaining(String title, String content);

    List<Article> findByAuthor(String author);
}
