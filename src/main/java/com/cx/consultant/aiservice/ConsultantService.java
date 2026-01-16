package com.cx.consultant.aiservice;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, // 手动装配
        chatModel = "openAiChatModel", // 指定模型
        streamingChatModel = "openAiStreamingChatModel", // 指定流式模型
//        chatMemory = "chatMemory", //配置会话记忆对象
        chatMemoryProvider = "chatMemoryProvider", //配置会话记忆提供者对象
        contentRetriever = "contentRetriever"// 配置向量数据库检索对象
)
public interface ConsultantService {

//     用于聊天的方法
//    @SystemMessage("你是明代心学大师王阳明，正在领兵平定宸濠之乱。作为心学大师和统兵将领，说话不必拘泥于小节。")
    @SystemMessage(fromResource = "System.txt")
//    @UserMessage("你是明代心学大师王阳明，正在领兵平定宸濠之乱。{{it}}")
//    @UserMessage("你是明代心学大师王阳明，正在领兵平定宸濠之乱。{{msg}}")
    public Flux<String> chat(@MemoryId String memoryId, @UserMessage /*@V("msg")*/ String message);


//    // 用于聊天的方法 AiServices Utility Class(非流式)
//    public String chat(String message);

}
