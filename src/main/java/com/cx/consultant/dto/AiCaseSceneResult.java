package com.cx.consultant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiCaseSceneResult {

    private String scenario;

    private String scenarioPlain;

    private List<QuestionDTO> questions;

}
