package com.leftbin.commons.kubernetes.mapper;

import com.leftbin.commons.proto.v1.english.rpc.enums.Word;
import com.leftbin.commons.proto.v1.kubernetes.resource.Pod;
import com.leftbin.commons.proto.v1.kubernetes.resource.PodContainer;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//todo: can we migrate this to use mapstruct?
@Component
public class PodMapper {
    public Pod generate(io.kubernetes.client.openapi.models.V1Pod kubernetesPod) {
        var podBuilder = Pod.newBuilder()
            .setNamespace(Objects.requireNonNull(Objects.requireNonNull(kubernetesPod.getMetadata()).getNamespace()))
            .setPodId(Objects.requireNonNull(Objects.requireNonNull(kubernetesPod.getMetadata()).getName()))
            .putAllLabels(kubernetesPod.getMetadata().getLabels())
            .setStatus(Objects.requireNonNull(Objects.requireNonNull(kubernetesPod.getStatus()).getPhase()));
        if (!Objects.isNull(kubernetesPod.getStatus())) {
            if (!Objects.isNull(kubernetesPod.getStatus().getReason())) {
                if (!"Running".equals(kubernetesPod.getStatus().getPhase())) {
                    podBuilder.setStatus(kubernetesPod.getStatus().getReason());
                }
            }
            if (!Objects.isNull(kubernetesPod.getStatus().getMessage())) {
                podBuilder.setStatusMessage(kubernetesPod.getStatus().getMessage());
            }
            if (!Objects.isNull(kubernetesPod.getStatus().getContainerStatuses())) {
                var pocContainerList = mapPodContainers(kubernetesPod.getStatus().getContainerStatuses());
                podBuilder.addAllContainers(pocContainerList)
                    .setContainersInReadyState(getReadyContainerCountString(kubernetesPod.getStatus().getContainerStatuses()))
                    .setContainersRestartCount(pocContainerList.stream().mapToInt(PodContainer::getRestartCount).sum());
            }
        }
        return podBuilder.build();
    }

    private List<PodContainer> mapPodContainers(List<V1ContainerStatus> containerStatuses) {
        var resp = new ArrayList<PodContainer>();
        containerStatuses.forEach(containerStatus -> {
            resp.add(PodContainer.newBuilder()
                .setRestartCount(containerStatus.getRestartCount())
                .setName(containerStatus.getName())
                .setImage(containerStatus.getImage())
                .setStatus(getContainerStatus(containerStatus))
                .setStatusMessage(getContainerStatusMessage(containerStatus))
                .build());
        });
        return resp;
    }

    private String getReadyContainerCountString(List<V1ContainerStatus> containerStatuses) {
        var readyCount = containerStatuses.stream().filter(V1ContainerStatus::getReady).count();
        return String.format("%s/%s", readyCount, containerStatuses.size());
    }

    private String getContainerStatus(V1ContainerStatus v1ContainerStatus) {
        if (Objects.isNull(v1ContainerStatus.getState())) {
            return Word.WordEnum.unknown.name();
        }
        if (!Objects.isNull(v1ContainerStatus.getState().getRunning())) {
            return "running";
        }
        if (!Objects.isNull(v1ContainerStatus.getState().getWaiting())
            && !Objects.isNull(v1ContainerStatus.getState().getWaiting().getReason())) {
            return v1ContainerStatus.getState().getWaiting().getReason();
        }
        if (!Objects.isNull(v1ContainerStatus.getState().getTerminated())
            && !Objects.isNull(v1ContainerStatus.getState().getTerminated().getReason())) {
            return v1ContainerStatus.getState().getTerminated().getReason();
        }
        return Word.WordEnum.unknown.name();
    }

    private String getContainerStatusMessage(V1ContainerStatus v1ContainerStatus) {
        if (Objects.isNull(v1ContainerStatus.getState())) {
            return Word.WordEnum.unknown.name();
        }
        if (!Objects.isNull(v1ContainerStatus.getState().getRunning())
            && !Objects.isNull(v1ContainerStatus.getState().getRunning().getStartedAt())) {
            return String.format("started at %s", v1ContainerStatus.getState().getRunning().getStartedAt());
        }
        if (!Objects.isNull(v1ContainerStatus.getState().getWaiting())
            && (!Objects.isNull(v1ContainerStatus.getState().getWaiting().getMessage()))) {
            return v1ContainerStatus.getState().getWaiting().getMessage();
        }
        if (!Objects.isNull(v1ContainerStatus.getState().getTerminated())) {
            if (!Objects.isNull(v1ContainerStatus.getState().getTerminated().getMessage())) {
                return v1ContainerStatus.getState().getTerminated().getMessage();
            }
            return "";
        }
        return Word.WordEnum.unknown.name();
    }
}
