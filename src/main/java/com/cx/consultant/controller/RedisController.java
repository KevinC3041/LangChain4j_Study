package com.cx.consultant.controller;

import com.cx.consultant.service.RedisCleanService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

        private final RedisCleanService redisCleanService;

        public RedisController(RedisCleanService redisCleanService) {
            this.redisCleanService = redisCleanService;
        }

        @DeleteMapping("/deleteByPrefix/{prefix}")
        public String deleteByPrefix(@PathVariable String prefix) {
            long deletedCnt = redisCleanService.deleteByPrefix(prefix);
            return "Deleted " + deletedCnt + " keys with prefix: " + prefix;
        }

}
