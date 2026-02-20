package com.cx.consultant.config;

//import com.cx.consultant.factory.RedisEmbeddingStoreFactory;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
//import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
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

import java.util.ArrayList;
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

    @Autowired
    private EmbeddingModel embeddingModel;

//    @Autowired
//    private RedisEmbeddingStoreFactory storeFactory;

//    @Autowired
//    private RedisEmbeddingStore redisEmbeddingStore;

    // 手工构建redisEmbeddingStore，设置向量数据前缀
//    @Bean
//    @Qualifier("cybergYangMingStore")
//    public RedisEmbeddingStore cybergYangMingRedisEmbeddingStore(){
//        return RedisEmbeddingStore.builder()
//                .indexName("cybergyangming")
//                .build();
//    }
    // 使用对象工厂实现多态前缀indexName
//    RedisEmbeddingStore redisEmbeddingStore = storeFactory.create("cybergyangming");

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

////    构建向量数据库操作对象，操作的是内存版本的向量数据库（拆出Ingest业务逻辑至RagIngestRunner）
//    @Bean("myEmbeddingStore")
//    public EmbeddingStore myEmbeddingStore(){// embeddingStore的对象，这个对象的名字不能和依赖自动注入的重复，所以这里要用myEmbeddingStore
//        return new InMemoryEmbeddingStore();
//    }

//    @Bean
//    public EmbeddingStore myEmbeddingStore(){// embeddingStore的对象，这个对象的名字不能和依赖自动注入的重复，所以这里要用myEmbeddingStore
////        1.加载文档进内存
////        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");
////        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content", new ApachePdfBoxDocumentParser());
//
////        1.1尝试按文件类型显示分流
//        List<Document> documents = new ArrayList<>();
//
//        TextDocumentParser textDocumentParser = new TextDocumentParser();
//
//        //        pdf
//        documents.addAll(
//                ClassPathDocumentLoader.loadDocuments(
//                        "content/pdfs",
//                        new ApachePdfBoxDocumentParser()
//                )
//        );
//
//        //        txt/md
//        documents.addAll(
//                ClassPathDocumentLoader.loadDocuments(
//                        "content/texts",
//                        new TextDocumentParser()
//                )
//        );
//
////        2.构建向量数据库操作对象
//        InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();
//
////        构建文档分割器对象
//        DocumentSplitter ds = DocumentSplitters.recursive(500, 100);
//
////        3.构建一个EmbeddingStoreIngestor对象
//        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//                .embeddingStore(store)
//                .documentSplitter(ds)
////                .embeddingModel(embeddingModel)
//                .build();
//        ingestor.ingest(documents);
//
//        return store;
//    }

////    构建向量数据库检索对象,2.20移除至RetrieverConfig
//    @Bean("contentRetriever")
//    public ContentRetriever contentRetriever(/*@Qualifier("myEmbeddingStore") EmbeddingStore store, */
//        @Qualifier("yangmingStore") RedisEmbeddingStore redisEmbeddingStore
//    ){
//        return EmbeddingStoreContentRetriever.builder()
////                .embeddingStore(store)
//                .embeddingStore(redisEmbeddingStore)
////                .minScore(0.5)
//                .minScore(0.3)
//                .maxResults(3)
//                .embeddingModel(embeddingModel)
//                .build();
//    }

}
