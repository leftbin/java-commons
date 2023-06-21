package com.leftbin.commons.lib.kafka.health;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.ThreadMetadata;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class StreamHealth {
    public static Boolean isRunning (KafkaStreams stream) {
        AtomicBoolean healthy = new AtomicBoolean(false);
        Predicate<KafkaStreams> checkIfStreamWorking = s -> s.state().isRunningOrRebalancing() || s.state().isShuttingDown();
        Predicate<ThreadMetadata> checkIfLocalThreadRunning =
                threadMetadata -> ((Objects.equals(threadMetadata.threadState(), "RUNNING"))
                        || (Objects.equals(threadMetadata.threadState(), "CREATED")));

        Optional.ofNullable(stream).filter(checkIfStreamWorking).ifPresent(o -> healthy.set(true));

        if (healthy.get()) {
            var notRunningLocalThreads = stream.metadataForLocalThreads().stream()
                    .filter(Predicate.not(checkIfLocalThreadRunning)).count();
            return notRunningLocalThreads <= 0;
        }
        return healthy.get();
    }
}
