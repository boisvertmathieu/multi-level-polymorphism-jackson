package com.example.demo;

import com.example.generated.model.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class ExternalAssetClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalAssetClient.class);

    private final WebClient externalApiWebClient;

    public ExternalAssetClient(WebClient externalApiWebClient) {
        this.externalApiWebClient = externalApiWebClient;
    }

    public Flux<Asset> fetchAssets() {
        log.debug("Fetching assets from external API...");
        return externalApiWebClient
                .get()
                .uri("/assets")
                .retrieve()
                .bodyToFlux(Asset.class)
                .doOnNext(asset -> log.debug("Received asset {} of type {}", asset.getId(), asset.getAssetType()));
    }
}
