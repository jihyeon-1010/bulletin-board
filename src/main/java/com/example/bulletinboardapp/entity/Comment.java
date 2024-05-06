package com.example.bulletinboardapp.entity;

import com.example.bulletinboardapp.dto.comment.response.CommentDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    @JsonIgnore
    private Article article;

    @ManyToOne
    private User user;

    private String author;

    @Column(length = 500)
    private String body;

    public static Comment createComment(CommentDto dto, User user, String author, Article article) {
        // 예외 발생
        if (dto.getId() != null) {
            throw new IllegalArgumentException("댓글 생성 실패: 댓글의 id가 없어야 합니다.");
        }
        if (dto.getArticleId() != article.getId()) {
            throw new IllegalArgumentException("댓글 생성 실패: 게시글의 id가 잘못됐습니다.");
        }

        // 엔티티 생성 및 반환
        return new Comment(dto.getId(), article, user, author, dto.getBody());
    }

    public void patch(String body) {
        // 객체 갱신
        if(body != null) {
            this.body = body;
        }
    }
}
