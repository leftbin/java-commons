package com.leftbin.commons.cloud.gcp.resource.project.mapper;

import com.leftbin.commons.proto.v1.cloud.gcp.resource.project.rpc.GcpProject;
import com.leftbin.commons.proto.v1.cloud.gcp.resource.project.state.GcpProjectState;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public abstract class GcpProjectStateAndRpcMapper {

    public abstract GcpProject generate(GcpProjectState input);

    public abstract GcpProjectState generate(GcpProject input);
}
