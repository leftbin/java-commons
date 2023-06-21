package com.leftbin.commons.lib.rpc.security.authorization.fga.dto;

import lombok.Data;

@Data
public class FgaCheckQueryRespDto {
    boolean allowed;
    String resolution;
}
