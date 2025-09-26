package com.example.demo.jackson;

import com.example.generated.model.Asset;
import com.example.generated.model.Boat;
import com.example.generated.model.Building;
import com.example.generated.model.Car;
import com.example.generated.model.Vehicle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class AssetPolymorphicDeserializerTest {

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
            .modules(new AssetPolymorphismModule())
            .build();

    @Test
    void carPayloadResolvesToCar() throws Exception {
        String json = "{\"id\":\"car-1\",\"assetType\":\"vehicle\",\"vehicleKind\":\"car\",\"name\":\"Test Car\"}";

        Asset asset = mapper.readValue(json, Asset.class);

        assertThat(asset).isInstanceOf(Car.class);
        assertThat(((Car) asset).getVehicleKind()).isEqualTo("car");
    }

    @Test
    void boatPayloadResolvesToBoat() throws Exception {
        String json = "{\"id\":\"boat-1\",\"assetType\":\"vehicle\",\"vehicleKind\":\"boat\",\"name\":\"Test Boat\"}";

        Asset asset = mapper.readValue(json, Asset.class);

        assertThat(asset).isInstanceOf(Boat.class);
        assertThat(((Boat) asset).getVehicleKind()).isEqualTo("boat");
    }

    @Test
    void buildingPayloadResolvesToBuilding() throws Exception {
        String json = "{\"id\":\"building-1\",\"assetType\":\"building\",\"name\":\"HQ\"}";

        Asset asset = mapper.readValue(json, Asset.class);

        assertThat(asset).isInstanceOf(Building.class);
    }

    @Test
    void unrecognisedVehicleDefaultsToVehicleBaseClass() throws Exception {
        String json = "{\"id\":\"vehicle-1\",\"assetType\":\"vehicle\",\"vehicleKind\":\"spaceship\",\"name\":\"Mystery\"}";

        Asset asset = mapper.readValue(json, Asset.class);

        assertThat(asset).isInstanceOfSatisfying(Vehicle.class, vehicle ->
                assertThat(vehicle.getVehicleKind()).isEqualTo("spaceship"));
    }

    @Test
    void unknownAssetTypeFallsBackToBaseAsset() throws Exception {
        String json = "{\"id\":\"unknown-1\",\"assetType\":\"gadget\",\"name\":\"Unknown\"}";

        Asset asset = mapper.readValue(json, Asset.class);

        assertThat(asset.getClass()).isEqualTo(Asset.class);
    }
}
