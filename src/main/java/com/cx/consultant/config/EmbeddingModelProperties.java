package com.cx.consultant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "embedding-model")
public class EmbeddingModelProperties {

    private String baseUrl;
    private String apiKey;
    private String modelName;
    private boolean logRequests;
    private boolean logResponses;

}
