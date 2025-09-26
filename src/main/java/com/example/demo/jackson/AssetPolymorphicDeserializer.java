package com.example.demo.jackson;

import com.example.generated.model.Asset;
import com.example.generated.model.Boat;
import com.example.generated.model.Building;
import com.example.generated.model.Car;
import com.example.generated.model.Vehicle;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * Custom deserializer that resolves {@link Asset} subtypes using the multi-level discriminator data
 * present in the external OpenAPI contract.
 */
public class AssetPolymorphicDeserializer extends StdDeserializer<Asset> {

    private static final Map<String, Class<? extends Asset>> ROOT_TYPE_MAPPING = Map.of(
            "building", Building.class,
            "vehicle", Vehicle.class
    );

    private static final Map<String, Class<? extends Asset>> VEHICLE_TYPE_MAPPING = Map.of(
            "car", Car.class,
            "boat", Boat.class
    );

    public AssetPolymorphicDeserializer() {
        super(Asset.class);
    }

    @Override
    public Asset deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        if (node == null || !node.isObject()) {
            throw JsonMappingException.from(parser, "Asset payload must be a JSON object");
        }

        String assetType = textValue(node.get("assetType"));
        if (assetType == null) {
            throw JsonMappingException.from(parser, "Missing 'assetType' discriminator");
        }

        Class<? extends Asset> targetType = resolveTargetType(assetType, node);
        try {
            return codec.treeToValue(node, targetType);
        } catch (JsonMappingException ex) {
            throw ex;
        } catch (IOException ex) {
            throw JsonMappingException.from(parser, "Unable to deserialize asset of type '" + assetType + "'", ex);
        }
    }

    private Class<? extends Asset> resolveTargetType(String assetType, JsonNode node) {
        Class<? extends Asset> rootTarget = ROOT_TYPE_MAPPING.get(assetType.toLowerCase(Locale.ROOT));
        if (rootTarget == null) {
            return Asset.class;
        }

        if (rootTarget.equals(Vehicle.class)) {
            String vehicleKind = textValue(node.get("vehicleKind"));
            if (vehicleKind != null) {
                Class<? extends Asset> vehicleTarget = VEHICLE_TYPE_MAPPING.get(vehicleKind.toLowerCase(Locale.ROOT));
                if (vehicleTarget != null) {
                    return vehicleTarget;
                }
            }
            return Vehicle.class;
        }

        return rootTarget;
    }

    private String textValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return value != null ? value.trim() : null;
    }
}
