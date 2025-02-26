package com.aminspire.domain.article.controller;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.service.ArticleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.service.TagService;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.global.common.response.CommonResponse;
import com.aminspire.global.security.AuthDetails;
import com.aminspire.infra.aop.RedisStore;
import com.aminspire.infra.config.redis.RedisDbTypeKey;
import com.aminspire.infra.config.redis.RedisStreamKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final TagService tagService;


    // 기사 검색
    @GetMapping("/search")
    @RedisStore(dbType = RedisDbTypeKey.KEYWORD_KEY, streamKey = RedisStreamKey.SEARCH_STREAM_KEY)
    public CommonResponse<?> searchArticles(@RequestParam(value = "query", required = true) String query) {
        List<ArticleInfoResponse.ArticleInfoItems> results = articleService.searchArticles(query);
        return CommonResponse.onSuccess(HttpStatus.OK.value(), results); // 200 OK
    }

    @GetMapping("/key-search")
    public CommonResponse<?> searchArticles(@AuthenticationPrincipal AuthDetails authDetails) {
        List<Map<String, Object>> results = new ArrayList<>();
        User user = authDetails.user();

        List<Tag> preferredTags = tagService.getUserPreferTags(user);
        List<String> queries = preferredTags.stream().map(Tag::getKeyword).toList(); // 키워드 리스트 추출

        if (queries.isEmpty()) {
            return CommonResponse.onSuccess(HttpStatus.OK.value(), "추천할 검색어 없음.");
        }

        for (String query : queries) {
            List<ArticleInfoResponse.ArticleInfoItems> resultForQuery = articleService.searchArticles(query);
            results.add(Map.of(
                    "keyword", query,
                    "articles", resultForQuery
            ));
        }

        return CommonResponse.onSuccess(HttpStatus.OK.value(), results);
    }
    // 특정 유저의 스크랩 저장
    @PostMapping("/scrap")
    public CommonResponse<Map<String, String>> scrapArticle(
            @AuthenticationPrincipal AuthDetails authDetails,
            @RequestBody ArticleInfoResponse.ArticleInfoItems articleInfoItems) {
        Map<String, String> result = new HashMap<>();

        User user = authDetails.user();

        articleService.saveArticle(user, articleInfoItems);
        result.put("message", "기사 스크랩 성공!");

        return CommonResponse.onSuccess(HttpStatus.CREATED.value(), result); // 201 CREATED
    }

    // 특정 유저의 스크랩 조회
    @GetMapping("/scrap")
    public CommonResponse<?> getScrapedArticles(@AuthenticationPrincipal AuthDetails authDetails) {
        User user = authDetails.user();

        List<Article> scrapedArticles = articleService.getArticlesByUser(user);

        return CommonResponse.onSuccess(HttpStatus.OK.value(), scrapedArticles); // 200 OK
    }

    // 특정 유저의 스크랩 삭제
    @DeleteMapping("/scrap")
    public CommonResponse<Map<String, String>> deleteScrapArticle(
            @AuthenticationPrincipal AuthDetails authDetails,
            @RequestParam Long newsId) {
        Map<String, String> result = new HashMap<>();
        User user = authDetails.user();
        articleService.deleteScrap(user, newsId);
        result.put("message", "기사 스크랩 삭제 성공!");
        return CommonResponse.onSuccess(HttpStatus.OK.value(), result); // 200 OK
    }
}
