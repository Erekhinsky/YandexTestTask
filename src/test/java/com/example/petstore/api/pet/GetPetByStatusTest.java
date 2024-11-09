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

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GetPetByStatusTest {
    private static final String URL = "https://petstore.swagger.io/v2/pet";
    private static PetClient petClient;
    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);
    private final Pet pet2 = new Pet(12304L, new Category(1234, "cats"), "Koshka", List.of("string"), List.of(new Tag(1234, "cats")), PetStatus.SOLD);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
    }

    @Test
    public void getBySingleStatus200Test() {
        petClient.deleteById(pet.getId(), "special-key");

        List<String> statuses = new ArrayList<>();
        statuses.add(PetStatus.AVAILABLE.getStatus());
        String res = String.join(",", statuses);

        Response response = petClient.getByStatuses(res);
        List<Pet> pets = response.jsonPath().getList("findAll {it.id == " + pet.getId() + "}");

        Assertions.assertEquals(0, pets.size());
        Assertions.assertEquals(200, response.getStatusCode());

        petClient.addPet(pet);

        response = petClient.getByStatuses(res);
        pets = response.jsonPath().getList("findAll {it.id == " + pet.getId() + "}");

        Assertions.assertEquals(1, pets.size());
        Assertions.assertEquals(200, response.getStatusCode());
    }

    @Test
    public void getByDoubleStatus200Test() {
        petClient.deleteById(pet.getId(), "special-key");
        petClient.deleteById(pet2.getId(), "special-key");

        List<String> statuses = new ArrayList<>();
        statuses.add(PetStatus.AVAILABLE.getStatus());
        statuses.add(PetStatus.SOLD.getStatus());
        String res = String.join(",", statuses);

        Response response = petClient.getByStatuses(res);
        List<Pet> pets = response.jsonPath().getList("findAll {it.id == " + pet.getId() + " || it.id == " + pet2.getId() + "}");

        Assertions.assertEquals(0, pets.size());
        Assertions.assertEquals(200, response.getStatusCode());

        petClient.addPet(pet);
        petClient.addPet(pet2);

        response = petClient.getByStatuses(res);
        pets = response.jsonPath().getList("findAll {it.id == " + pet.getId() + " || it.id == " + pet2.getId() + "}");

        Assertions.assertEquals(2, pets.size());
        Assertions.assertEquals(200, response.getStatusCode());
    }

    @Test
    public void getByInvalidStatus400Test() {
        Assertions.assertEquals(400, petClient.getByStatuses("res").getStatusCode());
    }

    @Test
    public void getWithoutStatus405Test() {
        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .get("/findByStatus")
                .then()
                .extract().response().statusCode());
    }
}
