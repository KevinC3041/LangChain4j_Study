package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasesEntity {

    private Long caseId;

    private String title;

    private String summary;

    private String category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
