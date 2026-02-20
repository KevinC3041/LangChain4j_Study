package com.cx.consultant.controller;

import com.cx.consultant.service.GameFlowService;
import com.cx.consultant.service.impl.GameFlowServiceImpl;
import entity.RedisTestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private GameFlowServiceImpl gameFlowService;

    @RequestMapping(value = "/caseGenerate", produces = "text/html;charset=UTF-8")
    public Flux<String> caseGenerate(String memoryId) { // 浏览器传递的用户问题
        return gameFlowService.startGame(memoryId);
    }


    @GetMapping("/getRedisValByKey")
    public String getRedisString(String key) {

        String value = template.opsForValue().get(key);
        log.info("key:{},value:{}", key, value);
        return value;

    }

    @PostMapping("/testRedisKeyValSave1")
    public void testRedisKeyValSave1(@RequestBody RedisTestEntity redisTestEntity) {
        String key = redisTestEntity.getKey();
        String value = redisTestEntity.getValue();
        template.opsForValue().set(key, value);
        log.info("key:{},value:{}", key, value);
    }

    @DeleteMapping("/testRedisDeleteByKey")
    public void testRedisDeleteByKey(@RequestBody RedisTestEntity redisTestEntity) {
        String key = redisTestEntity.getKey();
        template.delete(key);
        log.info("key:{} has been deleted.", key);
    }

    @PostMapping("/testRedisKeyValSave")
    public Map<String, Object> testRedisKeyValSave(
            @RequestParam String key,
            @RequestParam String value) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 关键：打印连接工厂信息
            RedisConnectionFactory factory = template.getConnectionFactory();
            result.put("factoryType", factory.getClass().getName());

            if (factory instanceof LettuceConnectionFactory) {
                LettuceConnectionFactory lettuce = (LettuceConnectionFactory) factory;
                result.put("host", lettuce.getHostName());
                result.put("port", lettuce.getPort());
                result.put("database", lettuce.getDatabase());
            }

            // 执行 set
            template.opsForValue().set(key, value);

            // 立即读取验证
            String retrieved = template.opsForValue().get(key);

            result.put("operation", "success");
            result.put("key", key);
            result.put("valueSent", value);
            result.put("valueRetrieved", retrieved);
            result.put("match", Objects.equals(value, retrieved));

            // 尝试直接执行 Redis 命令确认连接
            Long ttl = template.getExpire(key);
            result.put("ttl", ttl);

        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            result.put("stackTrace", e.getStackTrace()[0].toString());
        }

        return result;
    }

}
