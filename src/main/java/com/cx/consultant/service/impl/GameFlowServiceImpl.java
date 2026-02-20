package com.cx.consultant.service.impl;

import com.cx.consultant.aiservice.CaseSceneGeneratorService;
import com.cx.consultant.service.GameFlowService;
import entity.CasesEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class GameFlowServiceImpl implements GameFlowService {

    private final CasesServiceImpl casesService;
    private final CaseSceneGeneratorService caseSceneGenerator;

    public GameFlowServiceImpl(CasesServiceImpl casesService,
                               CaseSceneGeneratorService caseSceneGenerator) {
        this.casesService = casesService;
        this.caseSceneGenerator = caseSceneGenerator;
    }

    public Flux<String> startGame(String memoryId) {


        CasesEntity randomCase = casesService.randomSelectOne();
        String caseSummary = """
以下是一则现代案件资料：

%s

请改写为明代官场治理情境，
设置治理冲突与约束条件，
最后发问，不给答案。
""".formatted(randomCase.getSummary());

        log.info("caseSummary:\n{}", caseSummary);

        Flux<String> caseScene = caseSceneGenerator.chat(memoryId, caseSummary);
        return caseScene;

    }

}
