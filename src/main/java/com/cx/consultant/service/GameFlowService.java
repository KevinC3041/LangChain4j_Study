package com.cx.consultant.service;

import com.cx.consultant.dto.AiCaseSceneResult;
import com.cx.consultant.dto.CaseSceneGenerateResult;
import com.cx.consultant.dto.EvaluationResult;
import entity.CaseProgressEntity;
import entity.CaseQaEntity;
import entity.CaseSessionEntity;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GameFlowService {

//    public Flux<String> startGame(String memoryId);

    public CaseSceneGenerateResult startGame(String memoryId, int id);

    public CaseSceneGenerateResult generateScene(String memoryId, int id);

    public EvaluationResult evaluateAnswer(String memoryId, String message);

    public void quit(String memoryId);

    public void reset(String memoryId);

}
