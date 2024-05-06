package com.example.bulletinboardapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private LocalDate postDate;

    @Column(nullable = false)
    private long viewCount;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Comment> comments;

    public Article(User user, String author, String title, String content) {
        this.user = user;
        this.author = author;
        this.title = title;
        this.content = content;
        this.postDate = LocalDate.now();
    }

    public void patch(String title, String content) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }

    // 해당 게시글 조회수 증가
    public void incrementViews() {
        this.viewCount += 1;
    }
}
