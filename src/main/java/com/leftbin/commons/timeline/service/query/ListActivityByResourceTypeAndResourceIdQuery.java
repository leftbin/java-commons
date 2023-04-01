package com.leftbin.commons.timeline.service.query;

import com.leftbin.commons.proto.v1.timeline.rpc.ActivityList;
import com.leftbin.commons.proto.v1.timeline.rpc.ListActivityByResourceTypeAndResourceIdInput;
import com.leftbin.commons.rpc.query.IQuery;
import com.leftbin.commons.timeline.mapper.ActivityEntityAndRpcMapper;
import com.leftbin.commons.timeline.repo.ActivityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListActivityByResourceTypeAndResourceIdQuery implements IQuery<ListActivityByResourceTypeAndResourceIdInput, ActivityList> {

    private final ActivityRepo activityRepo;
    private final ActivityEntityAndRpcMapper activityEntityAndRpcMapper;

    @Override
    public QueryExecResult<ActivityList> execute(ListActivityByResourceTypeAndResourceIdInput input) {
        var builder = QueryExecResult.<ActivityList>builder().isExecuted(true);
        var outputBuilder = ActivityList.newBuilder();
        var activityEntites = activityRepo.findByResourceIdAndResourceTypeOrderByCreTsDesc(
                input.getResourceId(), input.getResourceType(), PageRequest.of(input.getPageInfo().getNum(), input.getPageInfo().getSize())
            );
        outputBuilder.setTotalPages(activityEntites.getTotalPages());
        activityEntites.forEach(activityEntity -> outputBuilder.addEntries(activityEntityAndRpcMapper.generate(activityEntity)));
        return builder.output(outputBuilder.build()).build();
    }
}
