package com.cx.consultant.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CommonConfig {

    @Autowired
    private OpenAiChatModel model;

    @Autowired
    @Qualifier("redisChatMemoryStore")
    private ChatMemoryStore redisChatMemoryStore;

    @Autowired
    @Qualifier("mySQLChatMemoryStore")
    private ChatMemoryStore mySQLChatMemoryStore;

//    @Bean
//    public ConsultantService consultantService(){
//        ConsultantService consultantService = AiServices.builder(ConsultantService.class)
//                .chatModel(model)
//                .build();
//
//        return consultantService;
//    }

//    构建会话记忆对象
    @Bean
    public ChatMemory chatMemory(){
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        return memory;
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
//                        .chatMemoryStore(redisChatMemoryStore)
                        .chatMemoryStore(mySQLChatMemoryStore)
                        .build();
            }
        };
        return chatMemoryProvider;
    }

//    构建向量数据库操作对象
    @Bean
    public EmbeddingStore myEmbeddingStore(){// embeddingStore的对象，这个对象的名字不能和依赖自动注入的重复，所以这里要用myEmbeddingStore
//        1.加载文档进内存
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");
//        2.构建向量数据库操作对象
        InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();
//        3.构建一个EmbeddingStoreIngestor对象
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .build();
        ingestor.ingest(documents);

        return store;
    }

//    构建向量数据库检索对象
    @Bean
    public ContentRetriever contentRetriever(@Qualifier("myEmbeddingStore") EmbeddingStore store){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .minScore(0.5)
                .maxResults(3)
                .build();
    }
}
