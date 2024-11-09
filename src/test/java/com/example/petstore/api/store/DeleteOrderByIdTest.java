package com.example.petstore.api.store;

import com.example.petstore.api.specification.Specifications;
import com.example.petstore.client.PetClient;
import com.example.petstore.client.StoreClient;
import com.example.petstore.data.entities.Category;
import com.example.petstore.data.entities.Order;
import com.example.petstore.data.entities.Pet;
import com.example.petstore.data.entities.Tag;
import com.example.petstore.data.enums.OrderStatus;
import com.example.petstore.data.enums.PetStatus;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;

public class DeleteOrderByIdTest {
    private static final String URL = "https://petstore.swagger.io/v2/store/order";
    private static StoreClient storeClient;
    private static PetClient petClient;

    private final Order order = new Order(1230L, 1230L, 1, OffsetDateTime.parse("2024-11-07T21:06:50.169Z"), OrderStatus.PLACED, false);
    private static final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);

    @BeforeAll
    static void prepareClient() {
        storeClient = new StoreClient();
        petClient = new PetClient();
        petClient.addPet(pet);
    }

    @Test
    public void deleteOrder200Test() {
        storeClient.addOrder(order);
        Assertions.assertEquals(200, storeClient.getOrderById(order.getId()).statusCode());

        Response response = storeClient.deleteById(order.getId());
        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertEquals(order.getId().toString(), response.getBody().path("message").toString());

        Assertions.assertEquals(404, storeClient.getOrderById(order.getId()).statusCode());
    }

    @Test
    public void deleteOrderWithoutOrderId405Test() {
        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .header("api_key", "special-key")
                .delete()
                .then()
                .extract().response().getStatusCode());
    }

    @Test
    public void deleteOrderInvalidOrderId405Test() {
        Assertions.assertEquals(405, storeClient.deleteById("petId").statusCode());
    }

    @Test
    public void deleteNoOrder404Test() {
        storeClient.addOrder(order);
        Assertions.assertEquals(200, storeClient.getOrderById(order.getId()).statusCode());

        storeClient.deleteById(order.getId());

        Assertions.assertEquals(404, storeClient.getOrderById(order.getId()).statusCode());
        Assertions.assertEquals(404, storeClient.deleteById(order.getId()).getStatusCode());
    }
}
