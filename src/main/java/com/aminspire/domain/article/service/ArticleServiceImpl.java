package com.aminspire.domain.article.service;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.repository.ArticleRepository;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.NaverNewsErrorCode;
import com.aminspire.infra.config.feign.NaverNewsFeignClient;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired private NaverNewsFeignClient naverNewsFeignClient;

    @Autowired private ArticleRepository articleRepository;

    public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("잘못된 요청: 검색어가 비어 있음");
            throw new CommonException(NaverNewsErrorCode.NAVER_API_CLIENT_ERROR);
        }

        try {
            // FeignClient를 통해 네이버 뉴스 API 요청
            ArticleInfoResponse response = naverNewsFeignClient.searchArticles(query);

            List<ArticleInfoResponse.ArticleInfoItems> results = response.getItems();

            if (results.isEmpty()) {
                log.info("검색 결과 없음");
                throw new CommonException(NaverNewsErrorCode.NAVER_API_EMPTY_RESPONSE);
            }

            return results;

        } catch (CommonException e) {
            // 예상 가능한 예외 (검색 결과 없음 등)
            log.warn("예상 가능한 예외 발생: {}", e.getMessage());
            throw e; // 그대로 다시 던짐

        } catch (Exception e) {
            // 예상하지 못한 예외
            log.error("기사 검색 중 예기치 못한 오류 발생: {}", e.getMessage(), e);
            throw new CommonException(NaverNewsErrorCode.NAVER_API_UNKNOWN_ERROR);
        }
    }


    @Override
    public boolean existsByLinkAndUserId(Long userId, String link) {
        return articleRepository.existsByUserIdAndLink(userId, link);
    }

    @Override
    public void scrapArticle(Article article) {
        articleRepository.save(article);
    }

    @Override
    public List<Article> getUserScraps(Long userId) {
        return articleRepository.findByUserId(userId);
    }

    @Override
    public void deleteScrap(Long userId, Long id) {
        // 특정 유저의 스크랩된 기사만 삭제하도록 검증
        Optional<Article> article = articleRepository.findByIdAndUserId(id, userId);

        if (article.isPresent()) {
            articleRepository.delete(article.get());
        } else {
            throw new IllegalArgumentException("해당 스크랩을 찾을 수 없습니다.");
        }
    }
}
