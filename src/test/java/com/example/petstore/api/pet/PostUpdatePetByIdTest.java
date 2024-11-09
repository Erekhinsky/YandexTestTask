package com.example.petstore.api.pet;

import com.example.petstore.client.PetClient;
import com.example.petstore.data.entities.Category;
import com.example.petstore.data.entities.Pet;
import com.example.petstore.data.entities.Tag;
import com.example.petstore.data.enums.PetStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class PostUpdatePetByIdTest {
    private static final String URL = "https://petstore.swagger.io/v2/pet";
    private static PetClient petClient;
    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
    }

    @Test
    public void updatePet200Test() {
        Response response = petClient.addPet(pet);

        String updName = "updatedName";
        PetStatus updStatus = PetStatus.PENDING;

        response = petClient.updatePetById(pet.getId(), updName, updStatus);
        Assertions.assertEquals(200, response.getStatusCode());

        response = petClient.getPetById(pet.getId());
        Pet tempPet = petClient.petFromResponse(response);

        Assertions.assertEquals(updName, tempPet.getName());
        Assertions.assertEquals(updStatus, tempPet.getStatus());
    }

    @Test
    public void updatePetWithoutStatus200Test() {

        petClient.addPet(pet);

        String updName = "updatedNameWithoutStatus";

        Response response = given()
                .baseUri(URL)
                .contentType(ContentType.URLENC)
                .param("name", updName)
                .post("/" + pet.getId())
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());

        response = petClient.getPetById(pet.getId());
        Pet tempPet = petClient.petFromResponse(response);

        Assertions.assertEquals(updName, tempPet.getName());
    }

    @Test
    public void updatePetInvalidPetId405Test() {
        String updName = "updatedNameWithInvalidPetId";
        PetStatus updStatus = PetStatus.PENDING;

        Assertions.assertEquals(405, petClient.updatePetById("petId", updName, updStatus).getStatusCode());
    }

    @Test
    public void updateNoPet404Test() {
        petClient.addPet(pet);
        petClient.deleteById(pet.getId(), "special-key");

        String updName = "updatedNameErr";
        PetStatus updStatus = PetStatus.SOLD;

        Assertions.assertEquals(404, petClient.getPetById(pet.getId()).statusCode());
        Assertions.assertEquals(404, petClient.updatePetById(pet.getId(), updName, updStatus).statusCode());
    }

    @Test
    public void updatePetInvalidName405Test() {
        PetStatus updStatus = PetStatus.SOLD;

        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .contentType(ContentType.URLENC)
                .param("status", updStatus)
                .post("/" + pet.getId())
                .then()
                .extract().response().getStatusCode());
    }

    @Test
    public void updatePetInvalidStatus405Test() {
        String updName = "updatedNameErr";

        Assertions.assertEquals(405, petClient.updatePetById(pet.getId(), updName, "updStatus").getStatusCode());
    }

    @Test
    public void updatePetWithoutPetId405Test() {
        String updName = "updatedNameErr";
        PetStatus updStatus = PetStatus.SOLD;

        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .contentType(ContentType.URLENC)
                .param("name", updName)
                .param("status", updStatus)
                .post("/")
                .then()
                .extract().response().getStatusCode());
    }
}
