package com.start.pawpal_finder.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SearchPostRepresentation {

    @JsonProperty
    private String keyword;
    @JsonProperty
    private String status;
    @JsonProperty
    private String tasks;
    @JsonProperty
    private List<String> dayOfWeek;
    @JsonProperty
    private String level;
    @JsonProperty
    private Double maxPricePerHour;
    @JsonProperty
    private Double maxPricePerDay;
    @JsonProperty
    private Double maxFlatRate;
}
