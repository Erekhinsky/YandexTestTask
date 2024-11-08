package com.example.petstore.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PetStatus {
    AVAILABLE("available"),
    PENDING("pending"),
    SOLD("sold");

    private final String status;

    PetStatus(String status) {
        this.status = status;
    }

    @JsonCreator
    public static PetStatus fromValue(String value) {
        for (PetStatus status : PetStatus.values())
            if (status.status.equalsIgnoreCase(value))
                return status;

        throw new IllegalArgumentException("Unknown status: " + value);
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}
