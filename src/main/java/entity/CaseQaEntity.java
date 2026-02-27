package entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("case_qa")
public class CaseQaEntity {

    @TableId(type = IdType.AUTO)
    private Long caseQaId;
    @TableField("case_session_id")
    private Long caseSessionId;
    @TableField("question")
    private String question;
    @TableField("question_type")
    private String questionType;
    @TableField("answer")
    private String answer;
    @TableField("evaluation")
    private String evaluation;
    @TableField("sub_score")
    private Integer subScore;
    @TableField("created_at")
    private LocalDateTime createdAt;

}
