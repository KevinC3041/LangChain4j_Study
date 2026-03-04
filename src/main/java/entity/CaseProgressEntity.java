package entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("case_progress")
public class CaseProgressEntity {

    @TableId(type = IdType.AUTO)
    private Long caseProgressId;
    @TableField("user_id")
    private String userId;
    @TableField("philosopher_code")
    private String philosopherCode;
    @TableField("current_case_session_id")
    private Long currentCaseSessionId;
    @TableField("stage")
    private String stage;
    @TableField("round_count")
    private int roundCount;
    @TableField("last_completed_session_id")
    private Long lastCompletedSessionId;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;

}
