package com.lbn.commons.security.fga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FgaListObjectsQueryRespDto {
    @JsonProperty("objects")
    List<String> objects;
}
