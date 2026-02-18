package com.cx.consultant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "langchain4j.community.redis")
@Component
public class LangchainRedisProperties {

    private String host;
    private Integer port;
    private String password;

}
