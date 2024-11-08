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

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class PostUploadImageTest {

    private static final String URL = "https://petstore.swagger.io/v2/pet";
    private static final String PET_IMAGE_PATH = "src/test/resources/api/image/";
    private static PetClient petClient;

    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
    }

    @Test
    public void uploadImage200Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        petClient.addPet(pet);

        String imageName = "animal.png";
        String additionalMetadata = "testData";

        Response response = petClient.uploadImageById(pet.getId(), additionalMetadata, PET_IMAGE_PATH + imageName);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("unknown", response.getBody().path("type"));
        assertThat(response.getBody().path("message").toString()).contains("File uploaded to ./" + imageName);
        assertThat(response.getBody().path("message").toString()).contains("testData");

        List<String> photos = petClient.getPetById(pet.getId()).jsonPath().getList("photoUrls");
        assertThat(imageName).isIn(photos);
    }

    @Test
    public void uploadImageOnlyPetId200Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        petClient.addPet(pet);

        Response response = given()
                .post("/" + pet.getId() + "/uploadImage")
                .then()
                .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("unknown", response.getBody().path("type"));
    }

    @Test
    public void uploadImageInvalidPetId405Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        String imageName = "blackCat.jpg";
        String additionalMetadata = "testData";

        Assertions.assertEquals(405, petClient.uploadImageById("petId", additionalMetadata, PET_IMAGE_PATH + imageName).getStatusCode());
    }

    @Test
    public void uploadImageWithoutPetId405Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        String imageName = "blackCat.jpg";
        String additionalMetadata = "testData";

        Assertions.assertEquals(405, given()
                .multiPart(new File(PET_IMAGE_PATH + imageName))
                .multiPart("additionalMetadata", additionalMetadata)
                .post()
                .then()
                .extract().response().getStatusCode());
    }

    @Test
    public void uploadImageNoPet404Test() {
        Specifications.initRequestSpecification(Specifications.requestSpecification(URL));

        petClient.addPet(pet);
        petClient.deleteById(pet.getId(), "special-key");

        String imageName = "blackCat.jpg";
        String additionalMetadata = "testData";

        Assertions.assertEquals(404, petClient.getPetById(pet.getId()).statusCode());

        Assertions.assertEquals(404, petClient.uploadImageById(pet.getId(), additionalMetadata, PET_IMAGE_PATH + imageName).statusCode());
    }
}
