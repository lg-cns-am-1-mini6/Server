package com.aminspire.domain.article.controller;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.service.ArticleService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
@RequestMapping("/articles")
public class ArticleController {
    @Autowired private ArticleService articleService;

    // 기사 검색
    @GetMapping("/search")
    public ResponseEntity<List<ArticleInfoResponse.ArticleInfoItems>> searchArticles(
            @RequestParam("query") String query) {
        try {
            List<ArticleInfoResponse.ArticleInfoItems> results =
                    articleService.searchArticles(query);

            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204
            }

            return ResponseEntity.ok(results); // 200
        } catch (Exception e) {
            log.error("기사 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // 스크랩 저장
    @PostMapping("/scrap")
    public ResponseEntity<Map<String, String>> scrapArticle(
            @RequestBody ArticleInfoResponse.ArticleInfoItems articleInfoItems) throws Exception {
        Map<String, String> result = new HashMap<>();

        // TODO: 유저 ID 받아오기 (임시 하드코딩, 실제 코드에서는 인증 객체에서 가져오기)
        Long userId = (long) 999;

        // 스크랩 중복 확인
        if (articleService.existsByLinkAndUserId(userId, articleInfoItems.getLink())) {
            result.put("message", "이미 스크랩된 기사입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        // Article 객체로 변환
        Article article = new Article();
        article.setTitle(articleInfoItems.getTitle());
        article.setLink(articleInfoItems.getLink());
        article.setDescription(articleInfoItems.getDescription());
        article.setPubDate(articleInfoItems.getPubDate());
        article.setUserId(userId);

        try {
            articleService.scrapArticle(article);
            result.put("message", "기사 스크랩 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("기사 스크랩 실패: {}", e.getMessage(), e);
            result.put("message", "기사 스크랩 실패");
            result.put("description", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // 스크랩 조회
    @GetMapping("/users/{userId}/scraps")
    public ResponseEntity<?> getUserScraps(@PathVariable Long userId) throws Exception {
        List<Article> articles = articleService.getUserScraps(userId);

        if (articles.isEmpty()) {
            Map<String, String> result = new HashMap<>();
            result.put("message", "스크랩한 기사가 없습니다.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
        return ResponseEntity.ok(articles);
    }

    // 스크랩 삭제
    @DeleteMapping("/users/{userId}/scraps/{id}")
    public ResponseEntity<Map<String, String>> deleteScrap(
            @PathVariable Long userId, @PathVariable Long id) throws Exception {
        Map<String, String> result = new HashMap<>();

        try {
            articleService.deleteScrap(userId, id);
            result.put("message", "스크랩 삭제 성공");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("message", "스크랩 삭제 실패");
            result.put("description", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
