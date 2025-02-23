package com.aminspire.domain.article.service;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.repository.ArticleRepository;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.NaverErrorCode;
import com.aminspire.global.exception.errorcode.ScrapErrorCode;
import com.aminspire.global.exception.errorcode.UserErrorCode;
import com.aminspire.global.security.jwt.JwtProvider;
import com.aminspire.infra.config.feign.NaverFeignClient;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired private NaverFeignClient naverFeignClient;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired JwtProvider jwtProvider;


    // 기사 검색
    public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("잘못된 요청: 검색어가 비어 있음");
            throw new CommonException(NaverErrorCode.NAVER_API_CLIENT_ERROR);
        }

        try {
            // FeignClient를 통해 네이버 뉴스 API 요청
            ArticleInfoResponse response = naverFeignClient.searchArticles(query);

            List<ArticleInfoResponse.ArticleInfoItems> results = response.getItems();

            if (results.isEmpty()) {
                log.info("검색 결과 없음");
                throw new CommonException(NaverErrorCode.NAVER_API_EMPTY_RESPONSE);
            }

            return results;

        } catch (CommonException e) {
            // 예상 가능한 예외 (검색 결과 없음 등)
            log.warn("예상 가능한 예외 발생: {}", e.getMessage());
            throw e; // 그대로 다시 던짐

        } catch (Exception e) {
            // 예상하지 못한 예외
            log.error("기사 검색 중 예기치 못한 오류 발생: {}", e.getMessage(), e);
            throw new CommonException(NaverErrorCode.NAVER_API_UNKNOWN_ERROR);
        }
    }
    
    // 유저 검증
    public User validateUser(String token) {
        String email = jwtProvider.getEmail(token);
        log.info("유저 email: {} 의 스크랩 저장 시작", email);

        // email이 null인 경우 예외를 던짐
        if (email == null) {
            throw new CommonException(UserErrorCode.USER_UNAUTHORIZED);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(UserErrorCode.USER_NOT_FOUND));
    }

    // 특정 유저의 스크랩 중복 검증
    @Override
    public boolean existsByUserAndLink(User user, String link) {
        return articleRepository.existsByUserAndLink(user, link);
    }

    // 특정 유저의 스크랩 저장
    @Transactional
    public void saveArticle(String token, ArticleInfoResponse.ArticleInfoItems articleInfoItems) {
        // 공통 검증 메서드 호출
        User user = validateUser(token);

        // 스크랩 중복 확인
        if (existsByUserAndLink(user, articleInfoItems.getLink())) {
            log.warn("중복 스크랩 시도: 유저 ID: {}, 기사 링크: {}", user.getId(), articleInfoItems.getLink());
            throw new CommonException(ScrapErrorCode.SCRAP_DUPLICATE);
        }

        // Article 객체로 변환
        Article article = new Article();
        article.setTitle(articleInfoItems.getTitle());
        article.setLink(articleInfoItems.getLink());
        article.setDescription(articleInfoItems.getDescription());
        article.setPubDate(articleInfoItems.getPubDate());
        article.setUser(user);

        try {
            articleRepository.save(article);
            log.info("기사 스크랩 성공: 유저 ID: {}, 기사 제목: {}", user.getId(), articleInfoItems.getTitle());
        } catch (Exception e) {
            log.error("기사 스크랩 실패: 유저 ID: {}, 오류: {}", user.getId(), e.getMessage(), e);
            throw new CommonException(ScrapErrorCode.SCRAP_SAVE_FAILED);
        }
    }

    // 특정 유저의 스크랩 조회
    @Transactional(readOnly = true)
    public List<Article> getArticlesByUser(String token) {
        // 유저 정보 조회 및 로그인 검증
        User user = validateUser(token);

        // 해당 유저가 스크랩한 기사 조회
        List<Article> articles = articleRepository.findByUser(user);

        if (articles.isEmpty()) {
            log.warn("유저 ID: {}의 스크랩된 기사가 없습니다.", user.getId());
            throw new CommonException(ScrapErrorCode.SCRAP_NOT_FOUND); // 예외 던짐
        }

        log.info("유저 ID: {}의 스크랩된 기사 조회 성공", user.getId());
        return articles;
    }

    // 특정 유저의 스크랩 삭제
    @Override
    public void deleteScrap(String token, Long newsId) {
        // 공통 검증 메서드 호출
        User user = validateUser(token);

        // 특정 유저의 스크랩된 기사만 삭제하도록 검증
        Optional<Article> article = articleRepository.findByIdAndUser(newsId, user);

        if (article.isPresent()) {
            articleRepository.delete(article.get());
            log.info("기사 스크랩 삭제 성공: 유저 ID: {}, 기사 ID: {}", user.getId(), newsId);
        } else {
            log.warn("삭제 실패: 해당 스크랩을 찾을 수 없습니다. 유저 ID: {}, 기사 ID: {}", user.getId(), newsId);
            throw new CommonException(ScrapErrorCode.SCRAP_NOT_FOUND);
        }
    }

}
