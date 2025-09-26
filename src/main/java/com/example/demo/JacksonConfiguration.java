package com.example.demo;

import com.example.demo.jackson.AssetPolymorphismModule;
import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Module assetPolymorphismModule() {
        return new AssetPolymorphismModule();
    }
}
