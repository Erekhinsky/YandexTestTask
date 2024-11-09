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

public class GetPetByIdTest {
    private static final String URL = "https://petstore.swagger.io/v2/pet";
    private static PetClient petClient;
    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
    }

    @Test
    public void getPetById200Test() {
        petClient.deleteById(pet.getId(), "special-key");
        petClient.addPet(pet);

        Response response = petClient.getPetById(pet.getId());
        Pet tempPet = petClient.petFromResponse(response);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(pet, tempPet);
    }

    @Test
    public void getPetByNegativeId400Test() {
        petClient.deleteById(pet.getId(), "special-key");

        long negativeId = -1234L;
        pet.setId(negativeId);
        petClient.addPet(pet);

        Assertions.assertEquals(400, petClient.getPetById(negativeId).getStatusCode());
    }

    @Test
    public void getPetByInvalidId400Test() {
        Assertions.assertEquals(400, petClient.getPetById("invalidId").getStatusCode());
    }

    @Test
    public void getPetWithoutId405Test() {
        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .get()
                .then()
                .extract().response().getStatusCode());
    }

    @Test
    public void getNoPet404Test() {
        petClient.deleteById(pet.getId(), "special-key");

        Assertions.assertEquals(404, petClient.getPetById(pet.getId()).getStatusCode());
    }
}
