package com.leftbin.commons.timeline.repo;

import com.leftbin.commons.timeline.entity.ActivityUpdateEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface ActivityUpdateRepo extends PagingAndSortingRepository<ActivityUpdateEntity, Long> {

    List<ActivityUpdateEntity> findByActivityId(String activityId);
}
