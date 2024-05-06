package com.example.bulletinboardapp.service.article;

import com.example.bulletinboardapp.dto.article.request.ArticleCreateRequest;
import com.example.bulletinboardapp.dto.article.request.ArticleUpdateRequest;
import com.example.bulletinboardapp.entity.Article;
import com.example.bulletinboardapp.entity.Comment;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.repository.ArticleRepository;
import com.example.bulletinboardapp.repository.CommentRepository;
import com.example.bulletinboardapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    // 게시글 작성
    @Transactional
    public Article create(ArticleCreateRequest request, Principal principal) {
        // 1. 현재 로그인한 사용자 정보 가져오기
        User user = userService.getCurrentUser(principal);

        // 2. DTO를 엔티티로 변환
        Article article = request.toEntity(user, principal.getName());

        // 3. 변환된 엔티티를 DB에 저장
        return articleRepository.save(article);
    }

    // 단일 게시글 조회
    @Transactional
    public Article show(Long id) {
        log.info("id = " + id);

        // 1. id를 조회해 데이터 가져오가
        Article article = articleRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        // 2. 게시글 조회수 증가
        if (article != null) {
            article.incrementViews();
        }

        return article;
    }

    // 내가 쓴 글
    @Transactional
    public List<Article> retrieveMyPosts(Principal principal) {
        // 1. 현재 로그인한 사용자 정보 가져오기
        String currentUser = principal.getName();

        // 2. 현재 인증된 사용자로 게시글 조회
        List<Article> articleList = articleRepository.findByAuthor(currentUser);

        // 3. 예외 처리
        if (articleList == null) {
            new IllegalArgumentException("해당 사용자의 게시글이 없습니다.");
        }

        // 4. id를 기준으로 내림차순 정렬
        articleList.sort(Comparator.comparing(Article::getId).reversed());

        return articleList;
    }

    // 댓글 단 글
    @Transactional
    public List<Article> retrieveMyComments(Principal principal) {
        // 1. 현재 로그인한 사용자 정보 가져오기
        String currentUser = principal.getName();

        // 2. 현재 인증된 사용자로 댓글 조회
        List<Comment> targetComment = commentRepository.findByAuthor(currentUser);

        // 3. 예외 처리
        if (targetComment == null) {
            new IllegalArgumentException("해당 사용자의 댓글이 없습니다.");
        }

        // 4. 댓글에서 article 객체를 추출해 내림차순으로 정렬 후 반환
        return targetComment.stream()
                .map(comment -> comment.getArticle())
                .sorted(Comparator.comparing(Article::getId).reversed())
                .collect(Collectors.toList());
    }

    // 모든 게시글 조회
    @Transactional
    public List<Article> index() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return articleRepository.findAll(sort);
    }

    // 페이징된 게시글 목록 조회
    @Transactional
    public Page<Article> getPagedArticles(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return articleRepository.findAll(pageable);
    }

    // 검색 게시글 조회
    @Transactional
    public List<Article> search(String query) {
       return articleRepository.findByTitleContainingOrContentContaining(query, query);
    }

    // 게시글 수정 폼
    @Transactional
    public Article edit(long id) {
        return articleRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
    }

    // 게시글 수정
    @Transactional
    public Article update(ArticleUpdateRequest request) {
        // 1. 타깃 조회하기
        Article target = articleRepository.findById(request.getId()).orElseThrow(() -> new IllegalArgumentException("잘못된 요청"));

        // 2. 업데이트 및 정상 응답(200)
        target.patch(request.getTitle(), request.getContent());
        Article updated = articleRepository.save(target);

        return updated;
    }


    // 게시글 삭제
    @Transactional
    public Article delete(long id) {
        // 1. 삭제할 대상 가져오기
        Article target = articleRepository.findById(id)
                        .orElseThrow(IllegalArgumentException::new);
        log.info(target.toString());

        // 2. 대상 엔티티 삭제하기
        articleRepository.delete(target);

        return target;
    }
}
