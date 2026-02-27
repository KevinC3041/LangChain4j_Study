package com.cx.consultant.service.impl;

import com.cx.consultant.aiservice.CaseSceneGeneratorService;
import com.cx.consultant.dto.CaseSceneGenerateResult;
import com.cx.consultant.service.GameFlowService;
import entity.CaseQaEntity;
import entity.CaseSessionEntity;
import entity.CasesEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GameFlowServiceImpl implements GameFlowService {

    private final CasesServiceImpl casesService;
    private final CaseSessionServiceImpl caseSessionService;
    private final CaseQaServiceImpl caseQaService;
    private final CaseSceneGeneratorService caseSceneGenerator;

    public GameFlowServiceImpl(CasesServiceImpl casesService,
                               CaseSessionServiceImpl caseSessionService,
                               CaseQaServiceImpl caseQaService,
                               CaseSceneGeneratorService caseSceneGenerator) {
        this.casesService = casesService;
        this.caseSessionService = caseSessionService;
        this.caseQaService = caseQaService;
        this.caseSceneGenerator = caseSceneGenerator;
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
//        Flux<String> caseScene = caseSceneGenerator.chat(memoryId, caseSummary);
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
//        Flux<String> caseScene = caseSceneGenerator.chat(memoryId, caseSummary);
//        return caseScene;
//
//    }


    public CaseSceneGenerateResult getCaseById(String memoryId, int id) {


        CasesEntity randomCase = casesService.getCaseById(id);
        String caseSummary = """
以下是一则现代案件资料：

%s

请改写为明代官场治理情境，
设置治理冲突与约束条件，
最后发问，不给答案。
""".formatted(randomCase.getSummary());

//        log.info("caseSummary:\n{}", caseSummary);

        CaseSceneGenerateResult caseScene = caseSceneGenerator.generate(memoryId, caseSummary);

//        log.info("caseScene:\n{}", caseScene.toString());
//        log.info("question1:\n{}", caseScene.getQuestions().get(0).getContent());
//        log.info("q1type:\n{}", caseScene.getQuestions().get(0).getType());
//        log.info("question2:\n{}", caseScene.getQuestions().get(1).toString());
//        log.info("q2type:\n{}", caseScene.getQuestions().get(1).getType());

        CaseSessionEntity caseSession = new CaseSessionEntity();
        // TODO:20260227
        caseSession.setUserId(memoryId);
        caseSession.setScenarioContent(caseScene.getScenario());
        caseSession.setCaseId(randomCase.getCaseId());
        caseSessionService.save(caseSession);

        Long caseSessionId = caseSession.getCaseSessionId();
        log.info("caseSessionId:{}", caseSessionId);

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

}
