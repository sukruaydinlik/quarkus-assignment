package com.sqills.assignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReadResponse {
    public Data data;

    public ReadResponse(String outputText) {
        this.data = new Data();
        this.data.outputText = outputText;
    }

    public static class Data {
        @JsonProperty("output_text")
        public String outputText;
    }
}
