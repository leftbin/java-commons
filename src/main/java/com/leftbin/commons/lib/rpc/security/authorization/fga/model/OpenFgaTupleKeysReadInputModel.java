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
public class OpenFgaTupleKeysReadInputModel {
    @JsonProperty("tuple_key")
    OpenFgaTupleKeyWriteModel tupleKey;
}
