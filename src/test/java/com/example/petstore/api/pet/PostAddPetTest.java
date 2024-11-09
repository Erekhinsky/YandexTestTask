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

public class PostAddPetTest {
    private static final String URL = "https://petstore.swagger.io/v2/pet";
    private static final String PET_JSON_PATH = "src/test/resources/api/json/pet/";
    private static PetClient petClient;

    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
    }

    @Test
    public void addPet200Test() {
        petClient.deleteById(pet.getId(), "special-key");

        Response response = petClient.addPet(pet);
        Pet tempPet = petClient.petFromResponse(response);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(pet, tempPet);
    }

    @Test
    public void addPetWithoutId200Test() {
        pet.setId(null);
        Response response = petClient.addPet(pet);
        Pet tempPet = petClient.petFromResponse(response);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertNotNull(tempPet.getId());
        tempPet.setId(null);
        Assertions.assertEquals(pet, tempPet);
    }

    @Test
    public void addPetAgain200Test() {
        petClient.deleteById(pet.getId(), "special-key");
        String newName = "Kotka";

        petClient.addPet(pet);

        pet.setName(newName);
        Response response = petClient.addPet(pet);
        Pet tempPet2 = petClient.petFromResponse(response);
        // Либо кидает ошибку (нет), либо не изменяет (тогда tempPet.name != tempPet2.name), либо изменяет

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(newName, tempPet2.getName());
    }

    @Test
    public void addPetNegativeId200Test() {
        long newId = -1230L;
        pet.setId(newId);
        petClient.deleteById(pet.getId(), "special-key");

        Response response = petClient.addPet(pet);
        Pet tempPet = petClient.petFromResponse(response);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotEquals(newId, tempPet.getId());
        pet.setId(tempPet.getId());
        Assertions.assertEquals(pet, tempPet);
    }

    @Test
    public void addPetInvalidPetId400Test() {
        Assertions.assertEquals(400, petClient.addPetFromJson(PET_JSON_PATH + "brokePetId.json").getStatusCode());
    }

    @Test
    public void addPetInvalidJson400Test() {
        Assertions.assertEquals(400, petClient.addPetFromJson(PET_JSON_PATH + "brokeJson.json").getStatusCode());
    }

    @Test
    public void addPetInvalidName405Test() {
        Assertions.assertEquals(405, petClient.addPetFromJson(PET_JSON_PATH + "brokeName.json").getStatusCode());
    }

    @Test
    public void addPetInvalidPhotoUrls405Test() {
        Assertions.assertEquals(405, petClient.addPetFromJson(PET_JSON_PATH + "brokePhotoUrls.json").getStatusCode());
    }

    @Test
    public void addPetInvalidStatus405Test() {
        Assertions.assertEquals(405, petClient.addPetFromJson(PET_JSON_PATH + "brokeStatus.json").getStatusCode());
    }

    @Test
    public void addPetWithoutBody405Test() {
        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .post()
                .then()
                .extract().response().getStatusCode());
    }
}
