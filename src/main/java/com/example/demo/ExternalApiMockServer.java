package com.example.demo;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ExternalApiMockServer {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiMockServer.class);

    private final ExternalApiProperties properties;
    private MockWebServer mockWebServer;

    public ExternalApiMockServer(ExternalApiProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void start() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if ("/assets".equals(request.getPath())) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody(sampleAssetPayload());
                }
                return new MockResponse().setResponseCode(404);
            }
        });
        mockWebServer.start();
        String resolvedBaseUrl = mockWebServer.url("/").toString();
        log.info("Mock external API started at {}", resolvedBaseUrl);
        properties.setBaseUrl(resolvedBaseUrl.substring(0, resolvedBaseUrl.length() - 1));
    }

    @PreDestroy
    void shutdown() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    private String sampleAssetPayload() {
        return "[" +
                "{\"id\":\"asset-car-1\",\"assetType\":\"vehicle\",\"vehicleKind\":\"car\",\"name\":\"City Car\",\"manufacturer\":\"Example Motors\",\"doors\":4,\"rangeKm\":320.5}," +
                "{\"id\":\"asset-boat-1\",\"assetType\":\"vehicle\",\"vehicleKind\":\"boat\",\"name\":\"Harbor Runner\",\"manufacturer\":\"Oceanic Works\",\"propulsion\":\"inboard\",\"maxKnots\":45.2}," +
                "{\"id\":\"asset-building-1\",\"assetType\":\"building\",\"name\":\"Headquarters\",\"floors\":12,\"location\":\"Downtown\"}" +
                "]";
    }
}
