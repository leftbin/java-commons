package com.leftbin.commons.timeline.repo;

import com.leftbin.commons.timeline.entity.ActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActivityRepo extends PagingAndSortingRepository<ActivityEntity, Long> {
    Page<ActivityEntity> findByResourceIdAndResourceTypeOrderByCreTsDesc(String resource, String resourceType, Pageable page);
}
