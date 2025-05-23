package com.imthath.rest_mcp_server;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class RestMcpServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ToolCallbackProvider restToolCallbackProvider() {
        return new RestToolCallbackProvider();
    }
} 