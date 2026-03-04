package com.cx.consultant.dto;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationResult {

    private SplitAnswers splitAnswers;
    private List<QuestionEvaluation> questionEvaluations;
    private Integer totalScore;
    private String overallEvaluation;
    private String overallEvaluationPlain;
    private Guidance guidance;

    @Data
    public static class SplitAnswers {
        private String governance;
        private String philosophy;
    }

    @Data
    public static class QuestionEvaluation {
        private String type;
        private Integer score;
        private String evaluation;
    }

    @Data
    public static class Guidance {
        private String governance;
        private String governancePlain;
        private String philosophy;
        private String philosophyPlain;
    }
}
