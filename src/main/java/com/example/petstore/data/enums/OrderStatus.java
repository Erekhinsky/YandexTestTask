package com.example.petstore.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PLACED("placed"),
    APPROVED("approved"),
    DELIVERED("delivered");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values())
            if (status.status.equalsIgnoreCase(value))
                return status;

        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
