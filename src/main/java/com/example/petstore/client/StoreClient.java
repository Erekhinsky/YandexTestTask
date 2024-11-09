package com.example.petstore.client;

import com.example.petstore.data.entities.Order;
import com.example.petstore.data.enums.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import static io.restassured.RestAssured.given;

public class StoreClient {

    private static final String URL = "https://petstore.swagger.io/v2/store";

    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0) // Устанавливаем значение по умолчанию для смещения
            .toFormatter();

    public Response getOrderById(Object orderId) {
        return given()
                .baseUri(URL + "/order")
                .get("/" + orderId)
                .then()
                .extract().response();
    }

    public Response addOrder(Object order) {
        return given()
                .baseUri(URL + "/order")
                .contentType(ContentType.JSON)
                .body(order)
                .post()
                .then()
                .extract().response();
    }

    public Response addOrderFromJson(String path) {
        return given()
                .baseUri(URL + "/order")
                .contentType(ContentType.JSON)
                .body(new File(path))
                .post()
                .then()
                .extract().response();
    }

    public Response getInventory() {
        return given()
                .baseUri(URL)
                .get("/inventory")
                .then()
                .extract().response();
    }

    public Response deleteById(Object orderId) {
        return given()
                .baseUri(URL + "/order")
                .delete("/" + orderId)
                .then()
                .extract().response();
    }

    public Order orderFromResponse(Response response) {
        return new Order(
                ((Number) response.getBody().path("id")).longValue(),
                ((Number) response.getBody().path("petId")).longValue(),
                ((Number) response.getBody().path("quantity")).intValue(),
                OffsetDateTime.parse(response.getBody().path("shipDate").toString(), formatter),
                OrderStatus.fromValue(response.getBody().path("status")),
                Boolean.getBoolean(response.getBody().path("complete").toString())
        );
    }
}
