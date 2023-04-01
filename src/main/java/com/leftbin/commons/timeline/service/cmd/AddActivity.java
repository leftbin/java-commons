package com.leftbin.commons.timeline.service.cmd;

import com.leftbin.commons.proto.v1.timeline.rpc.Activity;
import com.leftbin.commons.timeline.mapper.ActivityEntityAndRpcMapper;
import com.leftbin.commons.timeline.mapper.ActivityMapper;
import com.leftbin.commons.timeline.mapper.ActivityUpdateEntityAndRpcMapper;
import com.leftbin.commons.timeline.repo.ActivityRepo;
import com.leftbin.commons.timeline.repo.ActivityUpdateRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddActivity {
    private final ActivityRepo activityRepo;
    private final ActivityUpdateRepo activityUpdateRepo;
    private final ActivityUpdateEntityAndRpcMapper activityUpdateEntityAndRpcMapper;
    private final ActivityEntityAndRpcMapper activityEntityAndRpcMapper;

    public <T> Activity save(T previousState, T currentState, String resourceType, String resourceId) {
        if (Objects.isNull(previousState)) {
            // this is to save create activity
            try {
                var activity = ActivityMapper.generate(currentState, resourceType, resourceId);
                activityRepo.save(activityEntityAndRpcMapper.generate(activity));
                return activity;
            } catch (IllegalAccessException e) {
                log.info("unable to save timeline data due to : {}", e.getMessage());
            }
        }
        // this is for update/delete/restore activity
        try {
            var activity = ActivityMapper.generate(previousState, currentState, resourceType, resourceId);
            activityRepo.save(activityEntityAndRpcMapper.generate(activity));
            activityUpdateRepo.saveAll(activity.getUpdatesList().stream()
                .map(activityUpdateEntityAndRpcMapper::generate)
                .map(activityUpdateEntity -> {
                    activityUpdateEntity.setActivityId(activity.getActivityId());
                    return activityUpdateEntity;
                }).toList());
            return activity;
        } catch (IllegalAccessException e) {
            log.info("unable to save timeline data due to : {}", e.getMessage());
        }
        return Activity.newBuilder().build();
    }

}
