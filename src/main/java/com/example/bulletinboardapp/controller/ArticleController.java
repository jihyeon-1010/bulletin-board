package com.example.bulletinboardapp.controller;

import com.example.bulletinboardapp.config.jwt.TokenProvider;
import com.example.bulletinboardapp.dto.article.request.ArticleCreateRequest;
import com.example.bulletinboardapp.dto.article.request.ArticleUpdateRequest;
import com.example.bulletinboardapp.dto.comment.response.CommentDto;
import com.example.bulletinboardapp.entity.Article;
import com.example.bulletinboardapp.repository.UserRepository;
import com.example.bulletinboardapp.service.article.ArticleService;
import com.example.bulletinboardapp.service.comment.CommentService;
import com.example.bulletinboardapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final CommentService commentService;

    // 게시글 작성 페이지
    @GetMapping("/articles/new")
    public String newArticleForm() {
        return "articles/new";
    }

    // 게시글 작성
    @PostMapping("/articles/create")
    public String createArticle(@RequestBody ArticleCreateRequest request, Principal principal) {
        Article saved = articleService.create(request, principal);

        return "redirect:/articles/" + saved.getId();
    }

    // 단일 게시글 조회
    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model) {
        // 1. id를 조회해 데이터 가져오기
        Article articleEntity = articleService.show(id);
        List<CommentDto> commentsDtos = commentService.comments(id);

        // 2. 현재 로그인한 사용자 id를 가져와 게시글 작성자 id와 비교
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isCurrentUserArticleOwner = articleEntity.getAuthor().equals(userName);

        // 3. 현재 로그인한 사용자와 댓글 작성자가 같으면 댓글 수정 권한 부여
        commentsDtos.forEach(commentDto -> {
            if (commentDto.getAuthor().equals(userName)) {
                commentDto.isEditable();
            }
        });

        // 4. 모델에 데이터 등록
        model.addAttribute("article", articleEntity);
        model.addAttribute("commentDtos", commentsDtos);
        model.addAttribute("isCurrentUser", isCurrentUserArticleOwner);

        // 5. 뷰 페이지 반환
        return "articles/show";
    }

    // 내가 쓴 글
    @GetMapping("/articles/my-posts")
    public String retrieveMyPosts(Model model, Principal principal) {
        // 1. 현재 로그인한 사용자로 게시글 가져오기
        List<Article> articleList = articleService.retrieveMyPosts(principal);

        // 2. 모델에 데이터 등록
        model.addAttribute("articleList", articleList);

        // 3. 뷰 페이지 설정
        return "articles/my-posts";
    }

    // 댓글 단 글
    @GetMapping("/article/my-comments")
    public String retrieveMyComments(Model model, Principal principal) {
        // 1. 현재 로그인한 사용자 id의 댓글이 있는 게시글 가져오기
        List<Article> articleList = articleService.retrieveMyComments(principal);

        // 2. 중복된 ID를 가진 Article 제거 (하나의 게시물에 같은 사람이 여러 개의 댓글을 달았을 경우 대비)
        Set<Long> uniqueIds = new HashSet<>();
        List<Article> uniqueArticleList = new ArrayList<>();
        for (Article article : articleList) {
            if (uniqueIds.add(article.getId())) {
                uniqueArticleList.add(article);
            }
        }

        // 2. 모델에 데이터 등록
        model.addAttribute("articleList", uniqueArticleList);

        // 3. 뷰 페이지 설정
        return "articles/my-comments";
    }

    // 모든 게시글 조회
    @GetMapping("/articles")
    public String index(Model model, @RequestParam(defaultValue = "0") int page) {
        // 1. 한 페이지에 보여줄 게시글 수 설정
        int pageSize = 10;

        // 2. 현재 페이지에 해당하는 게시글 목록 조회
        Page<Article> pagedArticles = articleService.getPagedArticles(page, pageSize);
        List<Article> articleEntityList = pagedArticles.getContent();

        int totalPages = pagedArticles.getTotalPages();

        // 3. 페이지 번호 목록 생성
        List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());

        // 4. 이전 페이지, 다음 페이지 계산
        int prevPage = (page > 0) ? page - 1 : 0;
        int nextPage = (page < totalPages - 1) ? page + 1 : totalPages - 1;

        // 5. 모델에 데이터 등록
        model.addAttribute("articleList", articleEntityList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasPrevPage", page > 0);
        model.addAttribute("hasNextPage", page < totalPages - 1);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("pageNumbers", pageNumbers);

        // 뷰 페이지 설정
        return "articles/index";
    }

    // 검색 게시물 조회
    @GetMapping("/articles/query")
    public String search(@RequestParam String query, Model model) {
        // 1. 검색 게시물 가져오기
        List<Article> searchArticleEntity = articleService.search(query);

        // 2. 모델에 데이터 등록
        model.addAttribute("searchArticleList", searchArticleEntity);

        // 3. 뷰 페이지 설정
        return "articles/search";
    }

    // 게시글 수정 폼
    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable long id, Model model) {
        // 1. 해당 글의 id로 DB에서 데이터 가져오기
        Article articleEntity = articleService.edit(id);

        // 2. 모델에 데이터 등록
        model.addAttribute("article", articleEntity);

        // 3. 뷰 페이지 반환
        return "articles/edit";
    }

    // 게시글 수정
    @PostMapping("/articles/update")
    public String update(@RequestBody ArticleUpdateRequest request) {
        Article articleEntity = articleService.update(request);

        // 3. 수정 결과 페이지로 리다이렉트
        return "redirect:/articles/" + articleEntity.getId();
    }

    // 게시글 삭제
    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable long id, RedirectAttributes rttr) {
        // 1. 삭제할 대상 가져오기
        Article target = articleService.delete(id);

        // 2. 대상 엔티티 삭제
        if (target != null) {
            rttr.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
        }

        // 3. 결과 페이지로 리다이렉트
        return "redirect:/articles";
    }
}
