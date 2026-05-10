package com.sqills.assignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StoreRequest {
    public Data data;

    public static class Data {
        @JsonProperty("input_text")
        public String inputText;
    }
}
