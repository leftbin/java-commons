package com.lbn.commons.security.fga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenFgaListObjectsReqInputModel {
    String user;
    String relation;
    String type;
    @JsonProperty("contextual_tuples")
    OpenFgaContextualTuplesInputModel contextualTuples;
}
