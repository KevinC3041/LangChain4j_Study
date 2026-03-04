package com.cx.consultant.controller;

import com.cx.consultant.dto.AiCaseSceneResult;
import com.cx.consultant.dto.CaseSceneGenerateResult;
import com.cx.consultant.dto.EvaluationResult;
import com.cx.consultant.service.GameFlowService;
import com.cx.consultant.service.impl.GameFlowServiceImpl;
import entity.RedisTestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
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

//    @RequestMapping(value = "/caseGenerate", produces = "text/html;charset=UTF-8")
//    public Flux<String> caseGenerate(String memoryId) { // 浏览器传递的用户问题
//        return gameFlowService.startGame(memoryId);
//    }

//    @RequestMapping(value = "/getCaseById", produces = "text/html;charset=UTF-8")
//    public Flux<String> getCaseById(String memoryId, int id) { // 浏览器传递的用户问题
//        return gameFlowService.getCaseById(memoryId, id);
//    }

//   // Test1
//    @RequestMapping(value = "/getCaseById", produces = "text/html;charset=UTF-8")
//    public CaseSceneGenerateResult getCaseById(String memoryId, int id) { // 浏览器传递的用户问题
//        return gameFlowService.getCaseById(memoryId, id);
//    }

//   // Test2
    @GetMapping(value = "/getCaseById", produces = "text/html;charset=UTF-8")
    public Flux<String> getCaseById(String memoryId, int id) { // 浏览器传递的用户问题

        CaseSceneGenerateResult caseScene = gameFlowService.startGame(memoryId, id);
        String resultStr = String.format("governance question:<br>%s<br>philosophy question:<br>%s<br>scenario:<br>%s<br>scenarioPlain:<br>%s<br>governancePlain:<br>%s<br>philosophyPlain:<br>%s<br>", caseScene.getQuestions().get(0).getContent(), caseScene.getQuestions().get(1).getContent(), caseScene.getScenario(), caseScene.getScenarioPlain(), caseScene.getQuestions().get(0).getContentPlain(), caseScene.getQuestions().get(1).getContentPlain());

        String[] chars = resultStr.split("");
        return Flux
                .fromArray(chars)
                .delayElements(Duration.ofMillis(15));
    }



    @GetMapping(value = "/evaluateAnswer", produces = "text/html;charset=UTF-8")
    public Flux<String> evaluateAnswer(String memoryId, String message) { // 浏览器传递的用户问题
        EvaluationResult evaluationResult = gameFlowService.evaluateAnswer(memoryId, message);
        String governanceSubEvaluation = "";
        String philosophySubEvaluation = "";
        for (int i = 0; i < evaluationResult.getQuestionEvaluations().size(); i++) {
            EvaluationResult.QuestionEvaluation questionEvaluation = evaluationResult.getQuestionEvaluations().get(i);
            if (questionEvaluation.getType().equals("governance")) {
                governanceSubEvaluation = questionEvaluation.getEvaluation();
            } else if (questionEvaluation.getType().equals("philosophy")) {
                philosophySubEvaluation = questionEvaluation.getEvaluation();
            }
        }

//        String resultStr = String.format("%s%s%s", evaluationResult.getOverallEvaluation(), evaluationResult.getGuidance().getGovernance(), evaluationResult.getGuidance().getPhilosophy());
        String resultStr = String.format("governance点评:<br>%s<br>\nphilosophy点评:<br>%s<br>\noverallEvaluation:<br>%s<br>governance guidance:<br>%s<br>philosophy guidance:<br>%s<br>overallEvaluation白话:<br>%s<br>governance解惑白话:<br>%s<br>philosophy解惑白话:<br>%s", governanceSubEvaluation, philosophySubEvaluation, evaluationResult.getOverallEvaluation(), evaluationResult.getGuidance().getGovernance(), evaluationResult.getGuidance().getPhilosophy(), evaluationResult.getOverallEvaluationPlain(), evaluationResult.getGuidance().getGovernancePlain(), evaluationResult.getGuidance().getPhilosophyPlain());

        String[] chars = resultStr.split("");
        return Flux
                .fromArray(chars)
                .delayElements(Duration.ofMillis(15));
    }

    @GetMapping(value = "/reset", produces = "text/html;charset=UTF-8")
    public String reset(String memoryId) {
        gameFlowService.reset(memoryId);
        return "已重置";
    }

    @GetMapping(value = "/quit", produces = "text/html;charset=UTF-8")
    public String quit(String memoryId) {
        gameFlowService.quit(memoryId);
        return "已放弃";
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
