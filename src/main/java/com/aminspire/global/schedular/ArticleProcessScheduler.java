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

    private static final int BATCH_SIZE = 10; // ✅ 한 번에 처리할 기사 개수

    private final RedisClient redisClient;
    private final OpenAiClient openAiClient;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 10000) // ✅ 1분마다 실행
    public void processArticleKeywords() {
        log.info("[Scheduler] Redis Streams에서 ARTICLE 데이터 가져와서 키워드 추출 시작...");

        // ✅ Redis Streams에서 일정 개수만 가져옴
        List<MapRecord<String, Object, Object>> records = redisClient.readStream("KEYWORD", "ARTICLE", BATCH_SIZE);

        if (records.isEmpty()) {
            log.info("[Scheduler] 처리할 ARTICLE 데이터 없음.");
            return;
        }

        // ✅ 기사 데이터를 한꺼번에 모아 배치로 OpenAI 요청
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
            // ✅ OpenAI API 배치 요청
            Map<String, List<String>> extractedKeywords = openAiClient.generateBatchKeywordExtractPrompt(new ArrayList<>(articleTexts.values()));

            if (extractedKeywords.isEmpty()) {
                log.warn("[Scheduler] OpenAI에서 키워드 추출 실패 (빈 결과 반환)");
                return;
            }

            log.info("[Scheduler] OpenAI 배치 키워드 추출 완료");

            // ✅ 키워드를 DB에 저장
            for (Map.Entry<String, List<String>> entry : extractedKeywords.entrySet()) {
                String articleId = entry.getKey();
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
                        // ✅ 기존 키워드 존재하면 score 증가
                        Tag tag = existingTag.get();
                        tag.increaseScore();
                        tagRepository.save(tag);
                        log.info("[Scheduler] 기존 키워드 '{}'의 score 증가 (userEmail={})", keyword, userEmail);
                    } else {
                        // ✅ 새로운 키워드 저장
                        Tag newTag = new Tag(keyword, user, 1);
                        tagRepository.save(newTag);
                        log.info("[Scheduler] 새로운 키워드 '{}' 추가 (userEmail={})", keyword, userEmail);
                    }
                }

                // ✅ 처리 완료된 데이터 삭제 (ACK 없이 삭제)
                redisClient.deleteStreamRecord("KEYWORD", "ARTICLE", records.stream()
                        .filter(record -> record.getValue().get("articleId").equals(articleId))
                        .map(MapRecord::getId)
                        .toArray(RecordId[]::new));
                log.info("[Scheduler] Redis Streams에서 ARTICLE 데이터 삭제 완료: {}", articleId);
            }

        } catch (Exception e) {
            log.error("[Scheduler] OpenAI 키워드 추출 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
