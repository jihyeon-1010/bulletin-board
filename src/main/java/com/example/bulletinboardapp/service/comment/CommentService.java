package com.example.bulletinboardapp.service.comment;

import com.example.bulletinboardapp.dto.comment.request.CommentUpdateRequest;
import com.example.bulletinboardapp.dto.comment.response.CommentDto;
import com.example.bulletinboardapp.entity.Article;
import com.example.bulletinboardapp.entity.Comment;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.repository.ArticleRepository;
import com.example.bulletinboardapp.repository.CommentRepository;
import com.example.bulletinboardapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 조회
    @Transactional
    public List<CommentDto> comments(long articleId) {
        // 1. 결과 반환
        return commentRepository.findByArticleId(articleId)
                .stream()
                .map(CommentDto::createCommentDto)
                .collect(Collectors.toList());
    }

    // 댓글 생성
    @Transactional
    public CommentDto create(long articleId, CommentDto dto, Principal principal) {
        // 1. 게시글 조회 및 예외 발생
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패: " +
                        "대상 게시글이 없습니다."));

        // 2. 현재 로그인한 사용자 가져오기
        User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));;

        // 3. 댓글 엔티티 생성
        Comment comment = Comment.createComment(dto, user, principal.getName(), article);

        // 3. 댓글 엔티티를 DB에 저장
        Comment created = commentRepository.save(comment);

        // 4. DTO로 변환해 반환
        return CommentDto.createCommentDto(created);
    }

    // 댓글 수정
    @Transactional
    public CommentDto update(long id, CommentUpdateRequest dto) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 수정 실패: " +
                "대상 댓글이 없습니다."));

        // 2. 댓글 수정
        target.patch(dto.getBody());

        // 3. DB로 갱신
        Comment updated = commentRepository.save(target);

        // 4. 댓글 엔티티를 DTO로 변환 및 반환
        return CommentDto.createCommentDto(updated);
    }

    // 댓글 삭제
    @Transactional
    public CommentDto delete(long id) {
        // 1. 댓글 조회 및 예외 발생
        Comment target= commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 삭제 실패: " +
                        "대상 댓글이 없습니다."));

        // 2. 댓글 삭제
        commentRepository.delete(target);

        // 3. 삭제 댓글을 DTO로 변환 및 반환
        return CommentDto.createCommentDto(target);
    }

}
