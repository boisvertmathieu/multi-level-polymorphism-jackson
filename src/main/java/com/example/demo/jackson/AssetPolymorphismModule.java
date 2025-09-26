package com.example.demo.jackson;

import com.example.generated.model.Asset;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Jackson module that installs the {@link AssetPolymorphicDeserializer} for {@link Asset} payloads.
 */
public class AssetPolymorphismModule extends SimpleModule {

    public AssetPolymorphismModule() {
        super("asset-polymorphism-module");
        addDeserializer(Asset.class, new AssetPolymorphicDeserializer());
    }
}
