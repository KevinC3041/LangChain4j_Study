package com.cx.consultant.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class VectorStoreConfig {

    @Bean
    public UnifiedJedis unifiedJedis(LangchainRedisProperties properties) {
        return new UnifiedJedis(
                new HostAndPort(properties.getHost(), properties.getPort()),
                DefaultJedisClientConfig.builder()
                        .password(properties.getPassword())
                        .build()
        );
    }

    @Bean("yangmingStore")
    public RedisEmbeddingStore yangmingStore(
            UnifiedJedis jedis,
            EmbeddingModel embeddingModel
    ) {
        int dimension = embeddingModel
                .embed("dimension test")
                .content()
                .vector()
                .length;

        System.out.println("dimension: " + dimension);

        return RedisEmbeddingStore.builder()
                .unifiedJedis(jedis)
                .indexName("cybergyangming-index")
                .prefix("cybergyangming:")
                .dimension(dimension)
                .build();
    }

    @Bean("caseStore")
    public RedisEmbeddingStore caseStore(
            UnifiedJedis jedis,
            EmbeddingModel embeddingModel
    ) {

        int dimension = embeddingModel
                .embed("dimension test")
                .content()
                .vector()
                .length;

        return RedisEmbeddingStore.builder()
                .unifiedJedis(jedis)
                .indexName("case-index")
                .prefix("case:")
                .dimension(dimension)
                .build();
    }

}
