package entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("case_session")
public class CaseSessionEntity {

    @TableId(type = IdType.AUTO)
    private Long caseSessionId;
    @TableField("user_id")
    private String userId;
    @TableField("case_id")
    private Long caseId;
    @TableField("scenario_content")
    private String scenarioContent;
    @TableField("total_score")
    private Integer totalScore;
    @TableField("created_at")
    private LocalDateTime createdAt;

}
