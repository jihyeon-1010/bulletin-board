package com.example.bulletinboardapp.repository;

import com.example.bulletinboardapp.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 모든 댓글 조회
    @Query(value =
            "SELECT * " +
            "FROM comment " +
            "WHERE article_id = :articleId",
            nativeQuery = true)
    List<Comment> findByArticleId(Long articleId);

    List<Comment> findByAuthor(String author);
}
