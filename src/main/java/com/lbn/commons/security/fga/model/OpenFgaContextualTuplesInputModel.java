package com.lbn.commons.security.fga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenFgaContextualTuplesInputModel {
    @JsonProperty("tuple_keys")
    List<OpenFgaTupleKeyWriteModel> tupleKeys;
}
