package com.cx.consultant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseSceneGenerateResult {

    private String scenario;

    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {

        private String type;
        private String content;

    }


}
