package com.example.petstore.api.pet;

import com.example.petstore.client.PetClient;
import com.example.petstore.data.entities.Category;
import com.example.petstore.data.entities.Pet;
import com.example.petstore.data.entities.Tag;
import com.example.petstore.data.enums.PetStatus;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class PutUpdatePet {
    private static final String URL = "https://petstore.swagger.io/v2/pet";
    private static final String PET_JSON_PATH = "src/test/resources/api/json/pet/";
    private static PetClient petClient;

    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
    }

    @Test
    public void putPet200Test() {
        petClient.addPet(pet);

        String newName = "PutPetTest";
        pet.setName(newName);

        Response response = petClient.putPet(pet);
        Pet tempPet = petClient.petFromResponse(response);
        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(newName, tempPet.getName());
    }

    @Test
    public void putPetInvalidPetId400Test() {
        Assertions.assertEquals(400, petClient.putPetFromJson(PET_JSON_PATH + "brokePetId.json").getStatusCode());
    }

    @Test
    public void putPetInvalidName405Test() {
        Assertions.assertEquals(405, petClient.putPetFromJson(PET_JSON_PATH + "brokeName.json").getStatusCode());

    }

    @Test
    public void putPetInvalidPhotoUrls405Test() {
        Assertions.assertEquals(405, petClient.putPetFromJson(PET_JSON_PATH + "brokePhotoUrls.json").getStatusCode());
    }

    @Test
    public void putPetInvalidStatus405Test() {
        Assertions.assertEquals(405, petClient.putPetFromJson(PET_JSON_PATH + "brokeStatus.json").getStatusCode());
    }

    @Test
    public void putNoPet404Test() {
        petClient.addPet(pet);
        petClient.deleteById(pet.getId(), "special-key");
        Assertions.assertEquals(404, petClient.getPetById(pet.getId()).statusCode());

        Assertions.assertEquals(404, petClient.putPet(pet).getStatusCode());
    }

    @Test
    public void putPetWithoutBody405Test() {
        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .put()
                .then()
                .extract().response().getStatusCode());
    }

    @Test
    public void putPetInvalidJson400Test() {
        Assertions.assertEquals(400, petClient.putPetFromJson(PET_JSON_PATH + "brokeJson.json").getStatusCode());
    }
}
