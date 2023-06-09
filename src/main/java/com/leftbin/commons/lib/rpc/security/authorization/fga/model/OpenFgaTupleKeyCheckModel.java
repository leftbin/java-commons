package com.leftbin.commons.lib.rpc.security.authorization.fga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenFgaTupleKeyCheckModel {
    @JsonProperty("tuple_key")
    OpenFgaTupleKeyWriteModel tupleKey;
    @JsonProperty("contextual_tuples")
    OpenFgaContextualTuplesInputModel contextualTuples;
}
