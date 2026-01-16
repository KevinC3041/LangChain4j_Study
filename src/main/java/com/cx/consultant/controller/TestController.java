package com.cx.consultant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private StringRedisTemplate template;

    @GetMapping("/getRedisValByKey")
    public String getRedisString(String key) {

        String value = template.opsForValue().get(key);
        log.info("key:{},value:{}", key, value);
        return value;

    }

}
