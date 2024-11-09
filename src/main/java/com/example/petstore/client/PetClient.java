package com.example.petstore.client;

import com.example.petstore.data.entities.Category;
import com.example.petstore.data.entities.Pet;
import com.example.petstore.data.entities.Tag;
import com.example.petstore.data.enums.PetStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;

import static io.restassured.RestAssured.given;

public class PetClient {

    private static final String URL = "https://petstore.swagger.io/v2/pet";

    public Response getPetById(Object petId) {
        return given()
                .baseUri(URL)
                .get("/" + petId)
                .then()
                .extract().response();
    }

    public Response addPet(Object pet) {
        return given()
                .baseUri(URL)
                .contentType(ContentType.JSON)
                .body(pet)
                .post()
                .then()
                .extract().response();
    }

    public Response addPetFromJson(String path) {
        return given()
                .baseUri(URL)
                .contentType(ContentType.JSON)
                .body(new File(path))
                .post()
                .then()
                .extract().response();
    }

    public Response updatePetById(Object petId, Object name, Object status) {
        return given()
                .baseUri(URL)
                .contentType(ContentType.URLENC)
                .param("name", name)
                .param("status", status)
                .post("/" + petId)
                .then()
                .extract().response();
    }

    public Response getByStatuses(Object statuses) {
        return given()
                .baseUri(URL)
                .queryParam("status", statuses)
                .get("/findByStatus")
                .then()
                .extract().response();
    }

    public Response uploadImageById(Object petId, Object additionalMetadata, Object imagePath) {
        return given()
                .baseUri(URL)
                .contentType(ContentType.MULTIPART)
                .multiPart(new File(imagePath.toString()))
                .multiPart("additionalMetadata", additionalMetadata)
                .post("/" + petId + "/uploadImage")
                .then()
                .extract().response();
    }

    public Response putPet(Object pet) {
        return given()
                .baseUri(URL)
                .contentType(ContentType.JSON)
                .body(pet)
                .put()
                .then()
                .extract().response();
    }

    public Response putPetFromJson(Object path) {
        return given()
                .baseUri(URL)
                .contentType(ContentType.JSON)
                .body(new File(path.toString()))
                .put()
                .then()
                .extract().response();
    }

    public Response deleteById(Object petId, Object apiKey) {
        return given()
                .header("api_key", apiKey)
                .baseUri(URL)
                .delete("/" + petId)
                .then()
                .extract().response();
    }

    public Pet petFromResponse(Response response) {
        return new Pet(
                ((Number) response.getBody().path("id")).longValue(),
                new Category(response.getBody().path("category.id"), response.getBody().path("category.name")),
                response.getBody().path("name"),
                response.jsonPath().getList("photoUrls"),
                response.jsonPath().getList("tags", Tag.class),
                PetStatus.fromValue(response.getBody().path("status"))
        );
    }
}
