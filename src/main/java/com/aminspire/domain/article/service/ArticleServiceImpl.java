package com.aminspire.domain.article.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.global.config.NaverConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ArticleServiceImpl implements ArticleService {

	private final RestTemplate restTemplate;
    private final NaverConfig naverConfig;

    @Autowired
    public ArticleServiceImpl(RestTemplate restTemplate, NaverConfig naverConfig) {
        this.restTemplate = restTemplate;
        this.naverConfig = naverConfig;
    }

    @Override
    public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query) {
    	String clientId = naverConfig.getId();
        String clientSecret = naverConfig.getSecret();
        
        String keyword = URLEncoder.encode(query, StandardCharsets.UTF_8);

        URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com/")
                .path("v1/search/news.json")
                .queryParam("query", keyword)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode()
                .build()
                .toUri();

        RequestEntity<Void> req = RequestEntity.get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

        System.out.println("네이버 API 응답: " + resp.getBody());
        
        ObjectMapper om = new ObjectMapper();
        ArticleInfoResponse articleInfoResponse = null;

        try {
            articleInfoResponse = om.readValue(resp.getBody(), ArticleInfoResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return articleInfoResponse.getItems();
    }
}
