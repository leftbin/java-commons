package com.lbn.commons.rpc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Validator {
    private boolean isValid;
    private String msg;
}
