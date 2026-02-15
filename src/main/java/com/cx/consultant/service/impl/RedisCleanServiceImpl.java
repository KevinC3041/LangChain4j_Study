package com.cx.consultant.service.impl;

import com.cx.consultant.service.RedisCleanService;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCleanServiceImpl implements RedisCleanService {

    private final StringRedisTemplate redisTemplate;

    public RedisCleanServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long deleteByPrefix(String prefix) {

        long deletedCnt = 0;

        ScanOptions options = ScanOptions.scanOptions()
                .match(prefix + "*")
                .count(100)
                .build();

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(options)) {

            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                redisTemplate.delete(new String(key));
                deletedCnt++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deletedCnt;

    }

}
