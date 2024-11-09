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

public class GetOrderByIdTest {

    private static final String URL = "https://petstore.swagger.io/v2/store/order";

    private static PetClient petClient;
    private static StoreClient storeClient;

    private static final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.AVAILABLE);
    private final Order order = new Order(1230L, 1230L, 1, OffsetDateTime.parse("2024-11-07T21:06:50.169Z"), OrderStatus.PLACED, false);

    @BeforeAll
    static void prepareClient() {
        petClient = new PetClient();
        storeClient = new StoreClient();
        petClient.addPet(pet);
    }

    @Test
    public void getOrderById200Test() {
        storeClient.addOrder(order);

        Response response = storeClient.getOrderById(order.getId());
        Order tempOrder = storeClient.orderFromResponse(response);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(order, tempOrder);
    }

    @Test
    public void getOrderByNegativeId400Test() {
        storeClient.deleteById(order.getId());

        long negativeId = -1234L;
        order.setId(negativeId);
        storeClient.addOrder(order);

        Assertions.assertEquals(400, storeClient.getOrderById(negativeId).getStatusCode());
    }

    @Test
    public void getOrderByInvalidOrderId400Test() {
        Assertions.assertEquals(400, storeClient.getOrderById("invalidId").getStatusCode());
    }

    @Test
    public void getOrderWithoutOrderId405Test() {
        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .get()
                .then()
                .extract().response().getStatusCode());
    }

    @Test
    public void getNoOrder404Test() {
        storeClient.deleteById(order.getId());

        Assertions.assertEquals(404, storeClient.getOrderById(order.getId()).getStatusCode());
    }
}
