package com.aminspire.infra.config.redis;

import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.RedisErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamRecords;
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

    /**
     * ✅ Redis Stream에 데이터 추가
     */
    public String addToStream(String redisDb, String streamKey, Map<String, Object> data) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
            String recordId = redisTemplate.opsForStream()
                    .add(StreamRecords.newRecord().ofMap(data).withStreamKey(streamKey))
                    .getValue();
            log.info("[Redis] STREAM ADD [{}] stream={} data={} recordId={}", redisDb, streamKey, data, recordId);
            return recordId;
        } catch (Exception e) {
            log.error("[Redis] STREAM ADD Error: {}", e.getMessage(), e);
            throw new CommonException(RedisErrorCode.REDIS_ERROR);
        }
    }

    /**
     * ✅ Redis Stream에서 데이터 읽기
     */
    public List<MapRecord<String, Object, Object>> readStream(String redisDb, String streamKey) {
        try {
            RedisTemplate<String, Object> redisTemplate = getRedisTemplate(redisDb);
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(StreamOffset.latest(streamKey));
            log.info("[Redis] STREAM READ [{}] stream={} records={}", redisDb, streamKey, records);
            return records;
        } catch (Exception e) {
            log.error("[Redis] STREAM READ Error: {}", e.getMessage(), e);
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
