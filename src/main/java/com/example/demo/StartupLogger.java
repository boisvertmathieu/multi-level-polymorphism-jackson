package com.example.demo;

import com.example.generated.model.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
public class StartupLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @Bean
    ApplicationRunner logAssetsOnStartup(ExternalAssetClient externalAssetClient) {
        return args -> {
            Flux<Asset> assetFlux = externalAssetClient.fetchAssets();
            assetFlux.collectList().doOnNext(assets ->
                    assets.forEach(asset -> log.info("Loaded asset {} resolved as {}", asset.getId(), asset.getClass().getSimpleName()))
            ).block();
        };
    }
}
