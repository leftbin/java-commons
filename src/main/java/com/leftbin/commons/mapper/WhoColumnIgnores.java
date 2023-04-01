package com.leftbin.commons.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "who", ignore = true)
@Mapping(target = "mergeWho", ignore = true)
@Mapping(target = "evtTypeValue", ignore = true)
public @interface WhoColumnIgnores {
}
