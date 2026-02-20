package com.cx.consultant.aiservice;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, // 手动装配
        chatModel = "openAiChatModel", // 指定模型
        streamingChatModel = "openAiStreamingChatModel", // 指定流式模型
        chatMemoryProvider = "chatMemoryProvider" //配置会话记忆提供者对象
)
public interface CaseSceneGeneratorService {

    @SystemMessage(fromResource = "case-system.txt")
    Flux<String> chat(@MemoryId String memoryId, @UserMessage String message);


}
