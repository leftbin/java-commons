package com.lbn.commons.security.fga.dto;

import lombok.Data;

@Data
public class FgaCheckQueryRespDto {
    boolean allowed;
    String resolution;
}
