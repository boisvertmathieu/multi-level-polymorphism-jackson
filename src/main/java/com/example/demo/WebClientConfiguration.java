package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient externalApiWebClient(WebClient.Builder builder,
                                          ExternalApiProperties properties,
                                          ExternalApiMockServer externalApiMockServer) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();

        return builder
                .baseUrl(properties.getBaseUrl())
                .exchangeStrategies(strategies)
                .build();
    }
}
