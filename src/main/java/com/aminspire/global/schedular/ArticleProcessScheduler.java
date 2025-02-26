package com.aminspire.global.schedular;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.repository.TagRepository;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.openai.OpenAiClient;
import com.aminspire.infra.config.redis.RedisClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleProcessScheduler {

    private static final int BATCH_SIZE = 10;

    private final RedisClient redisClient;
    private final OpenAiClient openAiClient;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 10000)
    public void processArticleKeywords() {
        log.info("[Scheduler] Redis Streams에서 ARTICLE 데이터 가져와서 키워드 추출 시작...");

        List<MapRecord<String, Object, Object>> records =
                redisClient.readStream("KEYWORD", "ARTICLE", BATCH_SIZE);

        if (records.isEmpty()) {
            log.info("[Scheduler] 처리할 ARTICLE 데이터 없음.");
            return;
        }

        Map<String, String> articleTexts = new HashMap<>();
        Map<String, String> userEmails = new HashMap<>();

        for (MapRecord<String, Object, Object> record : records) {
            Map<Object, Object> data = record.getValue();
            String articleId = data.get("articleId").toString();
            String text = data.get("title") + " " + data.get("description");
            String userEmail = data.get("userEmail").toString();

            articleTexts.put(articleId, text);
            userEmails.put(articleId, userEmail);
        }

        log.info("[Scheduler] OpenAI 배치 키워드 추출 요청");

        try {

            List<String> batchInput = new ArrayList<>();
            for (Map.Entry<String, String> entry : articleTexts.entrySet()) {
                String combined = "[articleId=" + entry.getKey() + "]\n" + entry.getValue();
                batchInput.add(combined);
            }

            Map<String, List<String>> extractedKeywords =
                    openAiClient.generateBatchKeywordExtractPrompt(batchInput);

            if (extractedKeywords.isEmpty()) {
                log.warn("[Scheduler] OpenAI에서 키워드 추출 실패 (빈 결과 반환)");
                return;
            }

            log.info("[Scheduler] OpenAI 배치 키워드 추출 완료");

            for (Map.Entry<String, List<String>> entry : extractedKeywords.entrySet()) {
                String articleId = entry.getKey(); // JSON 응답의 key (기사 ID)
                List<String> keywords = entry.getValue();
                String userEmail = userEmails.get(articleId);

                Optional<User> userOptional = userRepository.findByEmail(userEmail);
                if (userOptional.isEmpty()) {
                    log.warn("[Scheduler] 해당 이메일을 가진 사용자를 찾을 수 없음: {}", userEmail);
                    continue;
                }
                User user = userOptional.get();

                for (String keyword : keywords) {
                    Optional<Tag> existingTag = tagRepository.findByKeywordAndSearcher(keyword, user);
                    if (existingTag.isPresent()) {
                        Tag tag = existingTag.get();
                        tag.increaseSearchCount();
                        tagRepository.save(tag);
                        log.info("[Scheduler] 기존 키워드 '{}'의 score 증가 (userEmail={})", keyword, userEmail);
                    } else {
                        Tag newTag = Tag.createTag(keyword, user);
                        tagRepository.save(newTag);
                        log.info("[Scheduler] 새로운 키워드 '{}' 추가 (userEmail={})", keyword, userEmail);
                    }
                }

                redisClient.deleteStreamRecord(
                        "KEYWORD",
                        "ARTICLE",
                        records.stream()
                                .filter(r -> r.getValue().get("articleId").equals(articleId))
                                .map(MapRecord::getId)
                                .toArray(RecordId[]::new)
                );
                log.info("[Scheduler] Redis Streams에서 ARTICLE 데이터 삭제 완료: {}", articleId);
            }

        } catch (Exception e) {
            log.error("[Scheduler] OpenAI 키워드 추출 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
