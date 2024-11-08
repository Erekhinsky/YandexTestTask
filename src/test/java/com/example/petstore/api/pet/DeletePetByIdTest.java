package com.example.petstore.api.pet;

import com.example.petstore.api.specification.Specifications;
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

public class DeletePetByIdTest {

    private static final String URL = "https://petstore.swagger.io/v2/pet";
    private static PetClient petClient;

    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
    }

    @Test
    public void deletePet200Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        petClient.addPet(pet);
        Assertions.assertEquals(200, petClient.getPetById(pet.getId()).statusCode());

        Response response = petClient.deleteById(pet.getId(), "special-key");
        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertEquals(pet.getId().toString(), response.getBody().path("message").toString());

        Assertions.assertEquals(404, petClient.getPetById(pet.getId()).statusCode());
    }

    @Test
    public void deletePetWithoutKey200Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        petClient.addPet(pet);
        Assertions.assertEquals(200, petClient.getPetById(pet.getId()).statusCode());

        Response response = given()
                .delete("/" + pet.getId())
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(pet.getId().toString(), response.getBody().path("message").toString());
        Assertions.assertEquals(404, petClient.getPetById(pet.getId()).statusCode());
    }

    @Test
    public void deletePetWithoutPetId405Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        Assertions.assertEquals(405, given().
                header("api_key", "special-key")
                .delete()
                .then()
                .extract().response().getStatusCode());
    }

    @Test
    public void deletePetInvalidPetId405Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        Assertions.assertEquals(405, petClient.deleteById("petId", "special-key").statusCode());
    }

    @Test
    public void deleteNoPet404Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        petClient.addPet(pet);
        Assertions.assertEquals(200, petClient.getPetById(pet.getId()).statusCode());
        petClient.deleteById(pet.getId(), "special-key");

        Assertions.assertEquals(404, petClient.getPetById(pet.getId()).statusCode());
        Assertions.assertEquals(404, petClient.deleteById(pet.getId(), "special-key").getStatusCode());
    }
}
