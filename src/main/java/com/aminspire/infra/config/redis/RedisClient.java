package com.aminspire.infra.config.redis;

import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.RedisErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisClient {

    private final Map<String, RedisTemplate<String, Object>> redisTemplates;

    /**
     * ✅ Redis에 데이터를 저장
     */
    public void setValue(String redisDb, String key, String value, Long timeout) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(key, value, Duration.ofMillis(timeout));
            log.info("[Redis] SET [{}] key={} value={} timeout={}ms", redisDb, key, value, timeout);
        } catch (Exception e) {
            log.error("[Redis] SET Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }

    /**
     * ✅ Redis에서 값을 가져옴
     */
    public String getValue(String redisDb, String key) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            Object value = values.get(key);
            log.info("[Redis] GET [{}] key={} value={}", redisDb, key, value);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            log.error("[Redis] GET Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }

    /**
     * ✅ Redis에서 특정 키 삭제
     */
    public void deleteValue(String redisDb, String key) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
            redisTemplate.delete(key);
            log.info("[Redis] DELETE [{}] key={}", redisDb, key);
        } catch (Exception e) {
            log.error("[Redis] DELETE Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }

    /**
     * ✅ 특정 키 존재 여부 확인
     */
    public boolean checkExistsValue(String redisDb, String key) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
            boolean exists = redisTemplate.hasKey(key);
            log.info("[Redis] EXISTS [{}] key={} result={}", redisDb, key, exists);
            return exists;
        } catch (Exception e) {
            log.error("[Redis] EXISTS Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }

//    /**
//     * ✅ Redis Stream에 데이터 추가
//     */
//    public String addToStream(String redisDb, String streamKey, Map<String, Object> data) {
//        try {
//            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
//            String recordId = redisTemplate.opsForStream()
//                    .add(StreamRecords.newRecord().ofMap(data).withStreamKey(streamKey))
//                    .getValue();
//            log.info("[Redis] STREAM ADD [{}] stream={} data={} recordId={}", redisDb, streamKey, data, recordId);
//            return recordId;
//        } catch (Exception e) {
//            log.error("[Redis] STREAM ADD Error: {}", e.getMessage(), e);
//            throw new CommonException(RedisErrorCode.REDIS_ERROR);
//        }
//    }

    public String addToStream(String redisDb, String streamKey, Map<String, Object> data, long ttlMillis) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);

            // ✅ 데이터 Stream에 추가
            String recordId = redisTemplate.opsForStream()
                    .add(StreamRecords.newRecord().ofMap(data).withStreamKey(streamKey))
                    .getValue();

            String processedKey = "processed:" + recordId;
            redisTemplate.opsForValue().set(processedKey, "EXIST", Duration.ofMillis(ttlMillis));

            log.info("[Redis] STREAM ADD [{}] stream={} data={} recordId={} TTL={}ms",
                    redisDb, streamKey, data, recordId, ttlMillis);

            return recordId;
        } catch (Exception e) {
            log.error("[Redis] STREAM ADD Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }

    public List<MapRecord<String, Object, Object>> readStream(String redisDb, String streamKey, int batchSize) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);

            // ✅ 마지막으로 읽은 ID 추적 (기존 데이터 다시 읽을 수 있도록)
            String lastProcessedIdKey = "last_processed_id:" + streamKey;
            String lastProcessedId = (String) redisTemplate.opsForValue().get(lastProcessedIdKey);

            if (lastProcessedId == null || lastProcessedId.isEmpty()) {
                lastProcessedId = "0"; // 처음 실행하는 경우 0부터 시작
            }

            // ✅ 마지막 ID 이후의 데이터만 읽기
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(
                            StreamReadOptions.empty().count(batchSize),
                            StreamOffset.create(streamKey, ReadOffset.from(lastProcessedId)) // ✅ 마지막 ID 이후부터 조회
                    );

            if (!records.isEmpty()) {
                // ✅ 가장 마지막 ID 저장하여 중복 방지
                String newLastProcessedId = records.get(records.size() - 1).getId().getValue();
                redisTemplate.opsForValue().set(lastProcessedIdKey, newLastProcessedId);
                log.info("[Redis] STREAM READ [{}] stream={} lastProcessedId={} newLastProcessedId={}",
                        redisDb, streamKey, lastProcessedId, newLastProcessedId);
            }

            return records;
        } catch (Exception e) {
            log.error("[Redis] STREAM READ Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }




    public List<MapRecord<String, Object, Object>> readStreamWithoutDuplicate(String redisDb, String streamKey) {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);

        // ✅ Redis에서 마지막으로 읽은 ID 가져오기
        String lastProcessedIdKey = "last_processed_id:" + streamKey;
        String lastProcessedId = (String) redisTemplate.opsForValue().get(lastProcessedIdKey);

        if (lastProcessedId == null || lastProcessedId.isEmpty()) {
            lastProcessedId = "0"; // 처음 실행하는 경우 0부터 시작
        }

        try {
            // ✅ XREAD를 사용하여 마지막 ID 이후의 데이터만 읽기
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(StreamOffset.create(streamKey, ReadOffset.from(lastProcessedId)));

            if (!records.isEmpty()) {
                // ✅ 가장 마지막 ID를 저장하여 이후 중복 방지
                String newLastProcessedId = records.get(records.size() - 1).getId().getValue();
                redisTemplate.opsForValue().set(lastProcessedIdKey, newLastProcessedId);
                log.info("[Redis] STREAM READ [{}] stream={} lastProcessedId={} newLastProcessedId={}",
                        redisDb, streamKey, lastProcessedId, newLastProcessedId);
            }

            return records;
        } catch (Exception e) {
            log.error("[Redis] STREAM READ Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }
    public void deleteStreamRecord(String redisDb, String streamKey, RecordId... recordIds) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
            if (recordIds.length > 0) {
                redisTemplate.opsForStream().delete(streamKey, recordIds); // ✅ RecordId 가변 인자 전달
                log.info("[Redis] STREAM DELETE [{}] stream={} deletedRecordIds={}", redisDb, streamKey, recordIds);
            } else {
                log.info("[Redis] STREAM DELETE [{}] stream={} - No records to delete", redisDb, streamKey);
            }
        } catch (Exception e) {
            log.error("[Redis] STREAM DELETE Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }






    /**
     * ✅ RedisDbType 기반으로 해당 RedisTemplate 가져오기
     */
    private RedisTemplate<String, Object> getRedisTemplate(String redisDb) {
        RedisTemplate<String, Object> redisTemplate = redisTemplates.get(redisDb);
        if (redisTemplate == null) {
            log.error("[Redis] ERROR: '{}'에 대한 RedisTemplate을 찾을 수 없음", redisDb);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
        return redisTemplate;
    }
}
