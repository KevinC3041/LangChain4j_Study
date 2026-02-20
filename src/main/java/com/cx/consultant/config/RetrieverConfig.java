package com.cx.consultant.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetrieverConfig {

    @Bean("yangmingRetriever")
    public ContentRetriever yangmingRetriever(/*@Qualifier("myEmbeddingStore") EmbeddingStore store, */
            @Qualifier("yangmingStore") RedisEmbeddingStore redisEmbeddingStore,
            EmbeddingModel embeddingModel
    ){
        return EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(store)
                .embeddingStore(redisEmbeddingStore)
//                .minScore(0.5)
                .minScore(0.3)
                .maxResults(3)
                .embeddingModel(embeddingModel)
                .build();
    }

    @Bean("caseRetriever")
    public ContentRetriever caseRetriever(
            @Qualifier("caseStore") RedisEmbeddingStore redisEmbeddingStore,
            EmbeddingModel embeddingModel
    ) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(redisEmbeddingStore)
                .minScore(0.3)
                .maxResults(3)
                .embeddingModel(embeddingModel)
                .build();
    }

}
