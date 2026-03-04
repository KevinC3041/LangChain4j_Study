package com.cx.consultant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cx.consultant.aiservice.CaseSceneGeneratorService;
import com.cx.consultant.aiservice.EvaluationService;
import com.cx.consultant.dto.*;
import com.cx.consultant.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.CaseProgressEntity;
import entity.CaseQaEntity;
import entity.CaseSessionEntity;
import entity.CasesEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.Evaluation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

@Slf4j
@Service
public class GameFlowServiceImpl implements GameFlowService {

    private final CasesService casesService;
    private final CaseSessionService caseSessionService;
    private final CaseQaService caseQaService;
    private final CaseProgressService caseProgressService;
    private final CaseSceneGeneratorService caseAgent;
    private final EvaluationService evaluationAgent;

    public GameFlowServiceImpl(CasesService casesService,
                               CaseSessionService caseSessionService,
                               CaseQaService caseQaService,
                               CaseProgressService caseProgressService,
                               CaseSceneGeneratorService caseAgent,
                               EvaluationService evaluationAgent) {
        this.casesService = casesService;
        this.caseSessionService = caseSessionService;
        this.caseQaService = caseQaService;
        this.caseProgressService = caseProgressService;
        this.caseAgent = caseAgent;
        this.evaluationAgent = evaluationAgent;
    }

//    public Flux<String> startGame(String memoryId) {
//
//
//        CasesEntity randomCase = casesService.randomSelectOne();
//        String caseSummary = """
//以下是一则现代案件资料：
//
//%s
//
//请改写为明代官场治理情境，
//设置治理冲突与约束条件，
//最后发问，不给答案。
//""".formatted(randomCase.getSummary());
//
//        log.info("caseSummary:\n{}", caseSummary);
//
//        Flux<String> caseScene = caseAgent.chat(memoryId, caseSummary);
//        return caseScene;
//
//
//    }

//    public Flux<String> getCaseById(String memoryId, int id) {
//
//
//        CasesEntity randomCase = casesService.getCaseById(id);
//        String caseSummary = """
//以下是一则现代案件资料：
//
//%s
//
//请改写为明代官场治理情境，
//设置治理冲突与约束条件，
//最后发问，不给答案。
//""".formatted(randomCase.getSummary());
//
//        log.info("caseSummary:\n{}", caseSummary);
//
//        Flux<String> caseScene = caseAgent.chat(memoryId, caseSummary);
//        return caseScene;
//
//    }


    private void updateCaseProgress(CaseProgressEntity caseProgress, String stage) {

        caseProgress.setStage(stage);
        caseProgressService.updateById(caseProgress);

    }

    public CaseSceneGenerateResult startGame(String memoryId, int id) {

//        1.check CASE_PROGRESS
//        1.1.先根据userId和philosopherCode获取CaseProgressEntity
        String userId = memoryId;
        String philosopherCode = "王阳明";
        CaseProgressEntity caseProgress = caseProgressService.getOne(new QueryWrapper<CaseProgressEntity>()
                .eq("user_id", userId)
                .eq("philosopher_code", philosopherCode)
        );

//        log.info("caseProgress:\n{}", caseProgress);

        CaseSceneGenerateResult caseSceneGenerateResult = null;

//        1.2.若不存在，创建新的。若存在，检查STAGE是否为COMPLETED。若是，创建新的并更新；若不是，直接用caseProgress
        if (caseProgress == null) {
            caseSceneGenerateResult = generateScene(memoryId, id);
            Long caseSessionId = caseSceneGenerateResult.getCaseSessionId();

            caseProgress = new CaseProgressEntity();
            caseProgress.setCurrentCaseSessionId(caseSessionId);
            caseProgress.setUserId(memoryId);
            caseProgress.setPhilosopherCode(philosopherCode);
            caseProgress.setStage("WAITING_ANSWER");
            caseProgressService.save(caseProgress);
        } else if (caseProgress.getStage().equals("COMPLETED")) {
            caseSceneGenerateResult = generateScene(memoryId, id);
            Long caseSessionId = caseSceneGenerateResult.getCaseSessionId();

            caseProgress.setCurrentCaseSessionId(caseSessionId);
            caseProgress.setStage("WAITING_ANSWER");
            caseProgressService.updateById(caseProgress);
        } else {

            Long caseSessionId = caseProgress.getCurrentCaseSessionId();
//            log.info("caseSessionId:{}", caseSessionId);
            CaseSessionEntity caseSession = caseSessionService.getById(caseSessionId);
//            log.info("caseSession:\n{}", caseSession);
            List<CaseQaEntity> caseQas = caseQaService.list(new QueryWrapper<CaseQaEntity>()
                    .eq("case_session_id", caseSessionId)
            );
            List<QuestionDTO> questions = new ArrayList<>();
            for (int i = 0; i < caseQas.size(); i++) {
//                log.info("caseQa:\n{}", caseQas.get(i));
                QuestionDTO question = new QuestionDTO();
                question.setType(caseQas.get(i).getQuestionType());
                question.setContent(caseQas.get(i).getQuestion());
                questions.add(question);
            }
            caseSceneGenerateResult = new CaseSceneGenerateResult(
                    caseSessionId,
                    caseSession.getScenarioContent(),
                    caseSession.getScenarioContentPlain(),
                    questions
            );

        }

//        log.info("caseSceneGenerateResult:\n{}", caseSceneGenerateResult);
        return caseSceneGenerateResult;

    }

    public CaseSceneGenerateResult generateScene(String memoryId, int id) {


        CasesEntity randomCase = casesService.getCaseById(id);
        String caseSummary = """
以下是一则现代案件资料：

%s

请改写为明代官场治理情境，
设置治理冲突与约束条件，
最后发问，不给答案。
""".formatted(randomCase.getSummary());

//        log.info("caseSummary:\n{}", caseSummary);

        AiCaseSceneResult aiCaseScene = caseAgent.generate(memoryId, caseSummary);

//        log.info("caseScene:\n{}", caseScene.toString());
//        log.info("question1:\n{}", caseScene.getQuestions().get(0).getContent());
//        log.info("q1type:\n{}", caseScene.getQuestions().get(0).getType());
//        log.info("question2:\n{}", caseScene.getQuestions().get(1).toString());
//        log.info("q2type:\n{}", caseScene.getQuestions().get(1).getType());

        CaseSessionEntity caseSession = new CaseSessionEntity();
        // TODO:20260227
        caseSession.setUserId(memoryId);
        caseSession.setScenarioContent(aiCaseScene.getScenario());
        caseSession.setScenarioContentPlain(aiCaseScene.getScenarioPlain());
        caseSession.setCaseId(randomCase.getCaseId());
        caseSessionService.save(caseSession);

        Long caseSessionId = caseSession.getCaseSessionId();

        CaseSceneGenerateResult caseScene =
                new CaseSceneGenerateResult(
                        caseSessionId,
                        aiCaseScene.getScenario(),
                        aiCaseScene.getScenarioPlain(),
                        aiCaseScene.getQuestions()
                );

//        log.info("caseSessionId:{}", caseSessionId);

        List<CaseQaEntity> caseQas = new ArrayList<>();
        for (int i = 0; i < caseScene.getQuestions().size(); i++) {
            CaseQaEntity caseQa = new CaseQaEntity();
            caseQa.setCaseSessionId(caseSessionId);
            caseQa.setQuestion(caseScene.getQuestions().get(i).getContent());
            caseQa.setQuestionType(caseScene.getQuestions().get(i).getType());

            caseQas.add(caseQa);
        }

        caseQaService.saveBatch(caseQas);
        return caseScene;

    }

    private CaseProgressEntity getUserProgressByPhilosopher(String userId, String philosopherCode) {
        CaseProgressEntity caseProgress = caseProgressService.getOne(new QueryWrapper<CaseProgressEntity>()
                .eq("user_id", userId)
                .eq("philosopher_code", philosopherCode)
        );

        return caseProgress;
    }

    public EvaluationResult evaluateAnswer(String memoryId, String message) {

        // Get the latest scenarioContent and question by caseSessionId and stage for the current userId and the current philosopherCode.
        CaseProgressEntity caseProgress = getUserProgressByPhilosopher(memoryId, "王阳明");

        log.info("In evaluation stage, caseProgress:\n{}", caseProgress);

        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setUserAnswer(message);

        Long caseSessionId = caseProgress.getCurrentCaseSessionId();
        CaseSessionEntity caseSession = caseSessionService.getOne(new QueryWrapper<CaseSessionEntity>()
                .eq("case_session_id", caseSessionId)
        );

        String scenarioContent = caseSession.getScenarioContent();
        evaluationContext.setScenario(scenarioContent);

        List<CaseQaEntity> caseQas = caseQaService.list(new QueryWrapper<CaseQaEntity>()
                .eq("case_session_id", caseSessionId)
        );

        List<QuestionDTO> questions = new ArrayList<>();
        for (CaseQaEntity caseQa : caseQas) {
            QuestionDTO question = new QuestionDTO();
            question.setContent(caseQa.getQuestion());
            question.setType(caseQa.getQuestionType());
            questions.add(question);
        }
        evaluationContext.setQuestions(questions);

        EvaluationResult evaluationResult;
        try {

            ObjectMapper mapper = new ObjectMapper();
            String jsonInput = mapper.writeValueAsString(evaluationContext);

//            String finalPrompt = """
//你将收到一段 JSON 数据，包含：
//
//- scenario: 古代情境内容
//- questions: 两个问题（包含类型与内容）
//- userAnswer: 弟子的整体回答
//
//请基于该 JSON 数据进行评鉴。
//
//JSON 数据如下：
//%s
//""".formatted(jsonInput);

//            log.info("finalPrompt:\n{}", finalPrompt);

            log.info("jsonInput:\n{}", jsonInput);

            evaluationResult = evaluationAgent.evaluate(memoryId, jsonInput);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 序列化失败", e);
        }


        afterEvaluation(evaluationResult, caseSession, caseQas, caseProgress);

        return evaluationResult;
    }

    private void afterEvaluation(EvaluationResult evaluationResult, CaseSessionEntity caseSession, List<CaseQaEntity> caseQas, CaseProgressEntity caseProgress) {

//        Update CASE_SESSION
        String governanceSubEvaluation = "";
        Integer governanceScore = null;
        String philosophySubEvaluation = "";
        Integer philosophyScore = null;
        String governanceAnswer = evaluationResult.getSplitAnswers().getGovernance();
        String philosophyAnswer = evaluationResult.getSplitAnswers().getPhilosophy();

        for (int i = 0; i < evaluationResult.getQuestionEvaluations().size(); i++) {
            EvaluationResult.QuestionEvaluation questionEvaluation = evaluationResult.getQuestionEvaluations().get(i);
            if (questionEvaluation.getType().equals("governance")) {
                governanceSubEvaluation = questionEvaluation.getEvaluation();
                governanceScore = questionEvaluation.getScore();
            } else if (questionEvaluation.getType().equals("philosophy")) {
                philosophySubEvaluation = questionEvaluation.getEvaluation();
                philosophyScore = questionEvaluation.getScore();
            }
        }
        int totalScore = evaluationResult.getTotalScore();

        caseSession.setTotalScore(totalScore);
        caseSessionService.updateById(caseSession);

//        Update CASE_QA
        for (CaseQaEntity caseQa : caseQas) {
            if (caseQa.getQuestionType().equals("governance")) {
                caseQa.setEvaluation(governanceSubEvaluation);
                caseQa.setSubScore(governanceScore);
                caseQa.setAnswer(governanceAnswer);
            } else if (caseQa.getQuestionType().equals("philosophy")) {
                caseQa.setEvaluation(philosophySubEvaluation);
                caseQa.setSubScore(philosophyScore);
                caseQa.setAnswer(philosophyAnswer);
            }

            caseQaService.updateById(caseQa);
        }

//        Update CASE_PROGRESS
        Long caseSessionId = caseSession.getCaseSessionId();
        caseProgress.setLastCompletedSessionId(caseSessionId);
        caseProgress.setRoundCount(caseProgress.getRoundCount() + 1);
        caseProgress.setStage("COMPLETED");
        caseProgressService.updateById(caseProgress);
    }

    public void quit(String memoryId) {

        CaseProgressEntity caseProgress = getUserProgressByPhilosopher(memoryId, "王阳明");
        caseProgress.setStage("COMPLETED");
        caseProgressService.updateById(caseProgress);

    }

    public void reset(String memoryId) {

        CaseProgressEntity caseProgress = getUserProgressByPhilosopher(memoryId, "王阳明");
        caseProgress.setStage("WAITING_ANSWER");
        caseProgressService.updateById(caseProgress);

    }


}
