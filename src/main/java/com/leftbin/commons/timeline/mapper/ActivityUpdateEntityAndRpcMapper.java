package com.leftbin.commons.timeline.mapper;

import com.leftbin.commons.mapper.ProtoMetadataIgnores;
import com.leftbin.commons.proto.v1.timeline.rpc.ActivityUpdate;
import com.leftbin.commons.timeline.entity.ActivityUpdateEntity;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@Mapper(componentModel = "spring")
public abstract class ActivityUpdateEntityAndRpcMapper {

    @ProtoMetadataIgnores
    @Mapping(target = "fieldNameBytes", ignore = true)
    @Mapping(target = "fieldTypeBytes", ignore = true)
    @Mapping(target = "oldValueBytes", ignore = true)
    @Mapping(target = "newValueBytes", ignore = true)
    @Mapping(target = "addedValuesList", ignore = true)
    @Mapping(target = "removedValuesList", ignore = true)
    public abstract ActivityUpdate generate(ActivityUpdateEntity input);

    @AfterMapping
    public void customMapping(ActivityUpdateEntity input, @MappingTarget ActivityUpdate.Builder builder) {
        builder.addAllAddedValues(Arrays.asList(input.getAddedValues()));
        builder.addAllRemovedValues(Arrays.asList(input.getRemovedValues()));
    }

    @InheritInverseConfiguration
    public abstract ActivityUpdateEntity generate(ActivityUpdate input);

    @AfterMapping
    public void customMapping(ActivityUpdate input, @MappingTarget ActivityUpdateEntity.ActivityUpdateEntityBuilder builder) {
        builder.activityUpdateId(UUID.randomUUID().toString());
        builder.addedValues(input.getAddedValuesList().toArray(new String[0]));
        builder.removedValues(input.getRemovedValuesList().toArray(new String[0]));
    }
}
