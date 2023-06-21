package com.leftbin.commons.lib.rpc.security.authorization.fga.model;

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
public class OpenFgaTupleKeysReadResponseModel {
    @JsonProperty("tuples")
    List<OpenFgaTupleKeyReadModel> tuples;
    @JsonProperty("continuation_token")
    String continuationToken;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenFgaTupleKeyReadModel {
        @JsonProperty("key")
        OpenFgaTupleKeyWriteModel key;
        @JsonProperty("timestamp")
        String timestamp;
    }
}
