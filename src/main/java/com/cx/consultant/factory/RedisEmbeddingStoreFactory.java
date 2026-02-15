//package com.cx.consultant.factory;
//
//import com.cx.consultant.config.LangchainRedisProperties;
//import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RedisEmbeddingStoreFactory {
//
//    private final LangchainRedisProperties properties;
//
//    public RedisEmbeddingStoreFactory(LangchainRedisProperties properties) {
//        this.properties = properties;
//    }
//
//    public RedisEmbeddingStore create(String indexName) {
//        return RedisEmbeddingStore.builder()
//                .host(properties.getHost())
//                .port(properties.getPort())
//                .password(properties.getPassword())
//                .indexName(indexName)
//                .build();
//    }
//
//}
