package com.aminspire.global.schedular;

import com.aminspire.domain.tag.domain.Tag;
import com.aminspire.domain.tag.repository.TagRepository;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.infra.config.redis.RedisClient;
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

    private static final String STREAM_KEY = "search_stream"; // ✅ Redis Streams 키
    private static final String REDIS_KEY = "KEYWORD";
    /**
     * ✅ 5분마다 Redis Streams에서 데이터를 읽어와 `Tag` 엔티티로 저장
     */
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void processRedisStreamData() {
        log.info("[Scheduler] Redis Streams에서 검색 데이터를 불러오는 작업 시작...");

        try {
            // ✅ Redis Streams에서 데이터 읽기
            List<MapRecord<String, Object, Object>> streamRecords = redisClient.readStream(REDIS_KEY, STREAM_KEY);

            for (MapRecord<String, Object, Object> record : streamRecords) {
                Map<Object, Object> data = record.getValue();

                // ✅ keyword & userId 추출
                String keyword = (String) data.get("keyword");
                Long userId = Long.valueOf(data.get("userId").toString());

                // ✅ 사용자 정보 조회
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isEmpty()) {
                    log.warn("[Scheduler] 사용자 ID {}가 존재하지 않음. 스킵", userId);
                    continue;
                }
                User user = userOptional.get();

                // ✅ 기존에 동일한 키워드가 있는지 확인
                Optional<Tag> existingTag = tagRepository.findByKeywordAndSearcher(keyword, user);

                if (existingTag.isPresent()) {
                    // ✅ 이미 존재하는 경우 -> `score` 증가
                    Tag tag = existingTag.get();
                    tag.increaseScore();
                    tagRepository.save(tag);
                    log.info("[Scheduler] 기존 키워드 '{}'의 score 증가 (userId={})", keyword, userId);
                } else {
                    // ✅ 새로운 키워드 -> DB에 저장
                    Tag newTag = new Tag(keyword, user, 1);
                    tagRepository.save(newTag);
                    log.info("[Scheduler] 새로운 키워드 '{}' 추가 (userId={})", keyword, userId);
                }

                // ✅ 처리된 Redis Streams 데이터 삭제 (선택 사항)
                redisClient.deleteValue(REDIS_KEY, record.getId().getValue());
            }
        } catch (Exception e) {
            log.error("[Scheduler] Redis Streams 처리 중 오류 발생", e);
        }
    }
}
