package com.kinorify.media_svc.media_svc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient profileWebClient(WebClient.Builder builder,
            @Value("${services.profile.base-url}") String profileBaseUrl) {

        return builder
                .baseUrl(profileBaseUrl)
                .filter(
                        new ServletBearerExchangeFilterFunction()
                )
                .build();
    }

    @Bean
    public WebClient collectionWebClient(WebClient.Builder builder,
            @Value("${services.collection.base-url}") String collectionBaseUrl) {

        return builder
                .baseUrl(collectionBaseUrl)
                .filter(
                        new ServletBearerExchangeFilterFunction()
                )
                .build();
    }
}
