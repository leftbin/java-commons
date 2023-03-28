package com.lbn.commons.security.fga.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenFgaTupleKeyWriteModel {
    String user;
    String relation;
    String object;
}
