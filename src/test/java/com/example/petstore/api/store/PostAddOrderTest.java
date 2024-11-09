package com.example.petstore.api.store;

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

public class PostAddOrderTest {

    private static final String URL = "https://petstore.swagger.io/v2/store/order";
    private static final String PET_JSON_PATH = "src/test/resources/api/json/store/";
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
    public void addOrder200Test() {
        storeClient.deleteById(order.getId());

        Response response = storeClient.addOrder(order);
        Order tempOrder = storeClient.orderFromResponse(response);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(order, tempOrder);
    }

    @Test
    public void addOrderWithoutId200Test() {
        order.setId(null);
        storeClient.deleteById(order.getId());

        Response response = storeClient.addOrder(order);
        Order tempOrder = storeClient.orderFromResponse(response);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertNotNull(tempOrder.getId());
        tempOrder.setId(null);
        Assertions.assertEquals(order, tempOrder);
    }

    @Test
    public void addOrderAgain200Test() {
        int newQuantity = 2;
        storeClient.deleteById(order.getId());

        storeClient.addOrder(order);

        order.setQuantity(newQuantity);
        Response response = storeClient.addOrder(order);
        Order tempOrder = storeClient.orderFromResponse(response);
        // Либо кидает ошибку (нет), либо не изменяет (тогда tempPet.name != tempPet2.name), либо изменяет

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(newQuantity, tempOrder.getQuantity());
    }

    @Test
    public void addOrderNegativeId200Test() {
        long newId = -1230L;
        order.setId(newId);
        storeClient.deleteById(order.getId());

        Response response = storeClient.addOrder(order);
        Order tempOrder = storeClient.orderFromResponse(response);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotEquals(newId, tempOrder.getId());
        order.setId(tempOrder.getId());
        Assertions.assertEquals(order, tempOrder);
    }

    @Test
    public void addOrderInvalidOrderId400Test() {
        Assertions.assertEquals(400, storeClient.addOrderFromJson(PET_JSON_PATH + "brokeOrderId.json").getStatusCode());
    }

    @Test
    public void addOrderInvalidJson400Test() {
        Assertions.assertEquals(400, storeClient.addOrderFromJson(PET_JSON_PATH + "brokeJson.json").getStatusCode());
    }

    @Test
    public void addOrderInvalidShipDate405Test() {
        Assertions.assertEquals(405, storeClient.addOrderFromJson(PET_JSON_PATH + "brokeShipDate.json").getStatusCode());
    }

    @Test
    public void addOrderInvalidComplete405Test() {
        Assertions.assertEquals(405, storeClient.addOrderFromJson(PET_JSON_PATH + "brokeComplete.json").getStatusCode());
    }

    @Test
    public void addOrderInvalidStatus405Test() {
        Assertions.assertEquals(405, storeClient.addOrderFromJson(PET_JSON_PATH + "brokeStatus.json").getStatusCode());
    }

    @Test
    public void addOrderWithoutBody405Test() {
        Assertions.assertEquals(405, given()
                .baseUri(URL)
                .post()
                .then()
                .extract().response().getStatusCode());
    }

}
