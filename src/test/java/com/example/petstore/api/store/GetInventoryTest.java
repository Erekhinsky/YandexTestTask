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

public class GetInventoryTest {
    private static StoreClient storeClient;
    private static PetClient petClient;

    private final Order order = new Order(1230L, 1230L, 1, OffsetDateTime.parse("2024-11-07T21:06:50.169Z"), OrderStatus.PLACED, false);
    private final Pet pet = new Pet(1230L, new Category(123, "cats"), "Kochka", List.of("string"), List.of(new Tag(123, "cats")), PetStatus.SOLD);

    @BeforeAll
    static void prepareClient() {
        storeClient = new StoreClient();
        petClient = new PetClient();
    }

    @Test
    void getInventory200Test() {
        int quantity;
        PetStatus petStatus = PetStatus.SOLD;

        petClient.deleteById(pet.getId(), "special-key");
        storeClient.deleteById(order.getId());

        Response response = storeClient.getInventory();
        Assertions.assertEquals(200, response.statusCode());
        quantity = response.jsonPath().getInt(petStatus.getStatus());

        petClient.addPet(pet);
        storeClient.addOrder(order);

        response = storeClient.getInventory();
        Assertions.assertEquals(quantity + 1, response.getBody().jsonPath().getInt(petStatus.getStatus()));
        Assertions.assertEquals(200, response.statusCode());
    }
}
