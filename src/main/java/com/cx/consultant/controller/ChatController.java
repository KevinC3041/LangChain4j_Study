package com.cx.consultant.controller;

import com.cx.consultant.aiservice.ConsultantService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private ConsultantService service;

    @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(String message) { // 浏览器传递的用户问题
        return service.chat(message);
    }


////    AiServices工具类(非流式)
//    @RequestMapping("/chat")
//    public String chat(String message) { // 浏览器传递的用户问题
//        return service.chat(message);
//    }


//    // Spring整合LangChain4j
//    @Autowired
//    private OpenAiChatModel model;
//
//    @RequestMapping("/chat")
//    public String chat(String message) { // 浏览器传递的用户问题
//
//        String result = model.chat(message);
//        return result;
//
//    }

}
