package com.leftbin.commons.security.fga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FgaListObjectIdsQueryRespDto {
    @JsonProperty("object_ids")
    List<String> objectIds;
}
