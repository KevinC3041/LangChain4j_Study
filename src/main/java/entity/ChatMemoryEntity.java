package entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_memory")
public class ChatMemoryEntity {

    @TableId
    private String memoryId;

    private String content;

    private LocalDateTime updatedTime;

}
