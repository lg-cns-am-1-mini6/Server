package com.aminspire.domain.article.controller;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.service.ArticleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aminspire.domain.user.controller.UserController;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.global.common.response.CommonResponse;
import com.aminspire.global.security.AuthDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired private ArticleService articleService;
    @Autowired private UserController userController;

    // 기사 검색
    @GetMapping("/search")
    public CommonResponse<?> searchArticles(@RequestParam(value = "query", required = true) String query) {
        List<ArticleInfoResponse.ArticleInfoItems> results = articleService.searchArticles(query);
        return CommonResponse.onSuccess(HttpStatus.OK.value(), results); // 200 OK
    }
/*
    // 키워드 기사 검색
    @GetMapping("/key-search")
    public CommonResponse<?> searchArticles(@AuthenticationPrincipal AuthDetails authDetails) {
        List<Map<String, Object>> results = new ArrayList<>();

        List<String> queries = ; //키워드 DB 메서드 연결
        User user = authDetails.user();

        // 각 query에 대해 searchArticles 호출
        for (String query : queries) {
            List<ArticleInfoResponse.ArticleInfoItems> resultForQuery = articleService.searchArticles(query);
            results.add(Map.of(query, resultForQuery));  // query와 그에 해당하는 결과를 묶어서 추가
        }

        return CommonResponse.onSuccess(HttpStatus.OK.value(), results); // 200 OK
    }
*/
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
