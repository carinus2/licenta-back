package com.start.pawpal_finder.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchOwnerPostRepresentation {

    @JsonProperty
    private String keyword;

    @JsonProperty
    private String status;

    @JsonProperty
    private String task;

    @JsonProperty
    private String city;

    @JsonProperty
    private String county;
}
