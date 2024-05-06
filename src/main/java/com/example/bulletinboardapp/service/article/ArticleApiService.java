//package com.example.bulletinboardapp.service.article;
//
//import com.example.bulletinboardapp.config.CustomOAuth2User;
//import com.example.bulletinboardapp.dto.article.ArticleForm;
//import com.example.bulletinboardapp.entity.Article;
//import com.example.bulletinboardapp.entity.User;
//import com.example.bulletinboardapp.repository.ArticleRepository;
//import com.example.bulletinboardapp.repository.UserRepository;
//import com.example.bulletinboardapp.service.user.UserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.security.Principal;
//import java.util.List;
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class ArticleApiService {
//    private final ArticleRepository articleRepository;
//    private final UserRepository userRepository;
//    private final UserService userService;
//
//    // 목록 조회
//    @Transactional(readOnly = true)
//    public List<Article> index() {
//        return articleRepository.findAll();
//    }
//
//    // 단일 조회
//    @Transactional
//    public Article show(long id) {
//        return articleRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("게시글 조회 실패: 해당 id의 게시글이 없습니다."));
//    }
//
//    // 생성
//    @Transactional
//    public Article create(ArticleForm form, Principal principal) {
//        // 1. 현재 로그인한 사용자 정보 가져오기
//        User user = userService.getCurrentUser(principal);
//
//        // 2. DTO를 엔티티로 변환
//        Article article = form.toEntity(user);
//
//        // 3. 변환된 엔티티를 DB에 저장
//        return articleRepository.save(article);
//        }
//    }
//
//    // 수정
//    @Transactional
//    public Article update(long id, ArticleForm form) {
//        // 1. 타깃 조회하기
//        Article target = articleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 요청"));
//
//        // 2. 업데이트 및 정상 응답(200)
//        target.patch(form.getTitle(), form.getContent());
//        Article updated = articleRepository.save(target);
//        return updated;
//    }
//
//    // 삭제
//    @Transactional
//    public Article delete(long id) {
//        // 1. 대상 찾기
//        Article target = articleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));
//
//        // 3. 대상 삭제하기
//        articleRepository.delete(target);
//        return target;
//    }
//}
