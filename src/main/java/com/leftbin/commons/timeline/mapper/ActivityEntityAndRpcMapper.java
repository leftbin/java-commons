package com.leftbin.commons.timeline.mapper;

import com.google.protobuf.Timestamp;
import com.leftbin.commons.mapper.ProtoMetadataIgnores;
import com.leftbin.commons.proto.v1.timeline.rpc.Activity;
import com.leftbin.commons.time.util.TimestampUtil;
import com.leftbin.commons.timeline.entity.ActivityEntity;
import com.leftbin.commons.timeline.repo.ActivityUpdateRepo;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public abstract class ActivityEntityAndRpcMapper {

    @Autowired
    private ActivityUpdateRepo activityUpdateRepo;

    @Autowired
    private ActivityUpdateEntityAndRpcMapper activityUpdateEntityAndRpcMapper;

    @ProtoMetadataIgnores
    @Mapping(target = "activityIdBytes", ignore = true)
    @Mapping(target = "activityTypeValue", ignore = true)
    @Mapping(target = "actorBytes", ignore = true)
    @Mapping(target = "mergeCreTs", ignore = true)
    @Mapping(target = "resourceIdBytes", ignore = true)
    @Mapping(target = "resourceTypeBytes", ignore = true)
    @Mapping(target = "removeUpdates", ignore = true)
    @Mapping(target = "updatesList", ignore = true)
    @Mapping(target = "updatesOrBuilderList", ignore = true)
    @Mapping(target = "updatesBuilderList", ignore = true)
    public abstract Activity generate(ActivityEntity input);

    @AfterMapping
    public void customMapping(ActivityEntity input, @MappingTarget Activity.Builder builder) {
        builder.addAllUpdates(activityUpdateRepo.findByActivityId(input.getActivityId()).stream()
            .map(activityUpdateEntityAndRpcMapper::generate).toList()
        );
    }

    @InheritInverseConfiguration
    public abstract ActivityEntity generate(Activity input);

    public Timestamp map(java.sql.Timestamp value) {
        return TimestampUtil.convert(value);
    }

    public java.sql.Timestamp map(Timestamp value) {
        return TimestampUtil.convert(value);
    }
}
