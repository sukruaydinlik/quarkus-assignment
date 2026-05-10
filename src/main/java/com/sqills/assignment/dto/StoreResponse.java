package com.sqills.assignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StoreResponse {
    public Long id;

    public StoreResponse(Long id) {
        this.id = id;
    }
}
