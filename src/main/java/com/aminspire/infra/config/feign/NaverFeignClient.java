package com.aminspire.infra.config.feign;

import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverFeignClient", url = "${NAVER_URL}", configuration = NaverFeignConfig.class)
public interface NaverFeignClient {

    @GetMapping("/search/news.json")
    ArticleInfoResponse searchArticles(@RequestParam("query") String query);
}
