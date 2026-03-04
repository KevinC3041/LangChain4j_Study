package com.cx.consultant.dto;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationContext {

    private String scenario;

    private List<QuestionDTO> questions;

    private String userAnswer;

}
