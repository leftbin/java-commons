package com.leftbin.commons.cloud.gcp.resource.folder.mapper;

import com.leftbin.commons.proto.v1.cloud.gcp.resource.folder.rpc.GcpFolder;
import com.leftbin.commons.proto.v1.cloud.gcp.resource.folder.state.GcpFolderState;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public abstract class GcpFolderStateAndRpcMapper {

    public abstract GcpFolder generate(GcpFolderState input);

    public abstract GcpFolderState generate(GcpFolder input);
}
