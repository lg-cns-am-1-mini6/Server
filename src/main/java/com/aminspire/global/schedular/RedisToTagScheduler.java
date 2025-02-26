package com.aminspire.global.schedular;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.repository.TagRepository;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.infra.config.redis.RedisClient;
import com.aminspire.infra.config.redis.RedisDbTypeKey;
import com.aminspire.infra.config.redis.RedisStreamKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.stream.MapRecord;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisToTagScheduler {

    private final RedisClient redisClient;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    private static final String STREAM_KEY = RedisStreamKey.SEARCH_STREAM_KEY.getKey();
    private static final String DB_TYPE = RedisDbTypeKey.KEYWORD_KEY.getKey();

    /**
     * ✅ 5분마다 Redis Streams에서 데이터를 읽어와 `Tag` 엔티티로 저장
     */
    @Scheduled(fixedDelay = 10000) // 10초마다 실행
    public void processRedisStreamData() {
        log.info("[Scheduler] Redis Streams에서 검색 데이터를 불러오는 작업 시작...");

        try {
            // ✅ Redis Streams에서 데이터 읽기
            List<MapRecord<String, Object, Object>> streamRecords = redisClient.readStreamWithoutDuplicate(DB_TYPE, STREAM_KEY);

            if (streamRecords.isEmpty()) {
                log.info("[Scheduler] 처리할 새로운 Keyword 없음.");
                return; // ✅ 더 이상 처리할 데이터가 없으면 종료
            }

            for (MapRecord<String, Object, Object> record : streamRecords) {
                Map<Object, Object> data = record.getValue();

                // ✅ keyword & userId 추출
                String keyword = (String) data.get("keyword");
                String userEmail = (String) data.get("userEmail");

                // ✅ 사용자 정보 조회
                Optional<User> userOptional = userRepository.findByEmail(userEmail);
                if (userOptional.isEmpty()) {
                    log.warn("[Scheduler] 사용자 ID {}가 존재하지 않음.", userEmail);
                    continue;
                }
                User user = userOptional.get();

                // ✅ 기존에 동일한 키워드가 있는지 확인
                Optional<Tag> existingTag = tagRepository.findByKeywordAndSearcher(keyword, user);

                if (existingTag.isPresent()) {
                    // ✅ 이미 존재하는 경우 -> `score` 증가
                    Tag tag = existingTag.get();
                    tag.increaseSearchCount();
                    tagRepository.save(tag);
                    log.info("[Scheduler] 기존 키워드 '{}'의 score 증가 (userId={})", keyword, userEmail);
                } else {
                    // ✅ 새로운 키워드 -> DB에 저장
                    Tag newTag = Tag.createTag(keyword, user);
                    tagRepository.save(newTag);
                    log.info("[Scheduler] 새로운 키워드 '{}' 추가 (userId={})", keyword, userEmail);
                }

            }
        } catch (Exception e) {
            log.error("[Scheduler] Redis Streams 처리 중 오류 발생", e);
        }
    }
}
