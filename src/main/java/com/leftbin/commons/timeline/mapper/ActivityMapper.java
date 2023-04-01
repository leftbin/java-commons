package com.leftbin.commons.timeline.mapper;

import com.leftbin.commons.collections.CollectionsUtil;
import com.leftbin.commons.proto.v1.timeline.enums.ActivityType;
import com.leftbin.commons.proto.v1.timeline.rpc.Activity;
import com.leftbin.commons.proto.v1.timeline.rpc.ActivityUpdate;
import com.leftbin.commons.time.util.TimestampUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.changetype.PropertyChange;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityMapper {
    public static <T> Activity generate(T previousState, T currentState, String resourceType,
                                        String resourceId) throws IllegalAccessException {
        var builder = Activity.newBuilder();
        builder.setActivityId(UUID.randomUUID().toString());

        builder.setResourceId(resourceId);
        builder.setResourceType(resourceType);
        builder.setCreTs(TimestampUtil.convert((Timestamp) getFieldValue(currentState, "updTs")));
        builder.setActor((String) Objects.requireNonNull(getFieldValue(currentState, "updBy")));

        var currentStateIsActive = (boolean) getFieldValue(currentState, "isActive");
        var previousStateIsActive = (boolean) getFieldValue(previousState, "isActive");
        if (!currentStateIsActive && previousStateIsActive) {
            builder.setActivityType(ActivityType.ACTIVITY_TYPE_DELETED);
            return builder.build();
        } else if (currentStateIsActive && !previousStateIsActive) {
            builder.setActivityType(ActivityType.ACTIVITY_TYPE_RESTORED);
            return builder.build();
        }
        builder.setActivityType(ActivityType.ACTIVITY_TYPE_UPDATED);
        var diff = JaversBuilder.javers().build().compare(previousState, currentState);
        if (diff.hasChanges()) {
            builder.addAllUpdates(diff.getChangesByType(PropertyChange.class)
                .stream()
                .map(propertyChange -> {
                        var dataType = getFieldDataType(propertyChange.getRight());
                        var activityUpdateBuilder = ActivityUpdate.newBuilder()
                            .setFieldName(propertyChange.getPropertyName())
                            .setFieldType(dataType);
                        if (dataType.equals("string-array")) {
                            getStringArrayDelta(Arrays.asList((String[]) propertyChange.getLeft()),
                                Arrays.asList((String[]) propertyChange.getRight()), activityUpdateBuilder);
                        } else if (dataType.equals("string-array-array")) {
                            return getStringArrayDelta((String[][]) propertyChange.getLeft(), (String[][]) propertyChange.getRight());
                        } else {
                            activityUpdateBuilder.setOldValue(propertyChange.getLeft().toString())
                                .setNewValue(propertyChange.getRight().toString());
                        }
                        return List.of(activityUpdateBuilder.build());
                    }
                ).flatMap(List::stream).toList());
        }
        return builder.build();
    }

    public static <T> Activity generate(T currentState, String resourceType, String resourceId) throws IllegalAccessException {
        var builder = Activity.newBuilder();
        builder.setActivityId(UUID.randomUUID().toString());
        builder.setActivityType(ActivityType.ACTIVITY_TYPE_CREATED);
        builder.setResourceId(resourceId);
        builder.setResourceType(resourceType);
        builder.setCreTs(TimestampUtil.convert((Timestamp) getFieldValue(currentState, "creTs")));
        builder.setActor((String) getFieldValue(currentState, "creBy"));
        return builder.build();
    }

    private static void getStringArrayDelta(List<String> previousList, List<String> currentList,
                                            ActivityUpdate.Builder activityUpdateBuilder) {
        var addedValues = currentList.stream().filter(value -> !previousList.contains(value)).toList();
        var removedValues = previousList.stream().filter(value -> !currentList.contains(value)).toList();
        activityUpdateBuilder.addAllAddedValues(addedValues);
        activityUpdateBuilder.addAllRemovedValues(removedValues);
    }

    private static List<ActivityUpdate> getStringArrayDelta(String[][] previous, String[][] current) {
        var oldValue = CollectionsUtil.convert(previous);
        var newValue = CollectionsUtil.convert(current);
        var activityUpdates = new ArrayList<ActivityUpdate>();
        activityUpdates.addAll(oldValue.entrySet().stream()
            .filter(entry -> !newValue.getOrDefault(entry.getKey(), "").equals(entry.getValue()))
            .map(entry -> ActivityUpdate.newBuilder()
                .setFieldName(entry.getKey())
                .setFieldType("string")
                .setOldValue(entry.getValue())
                .setNewValue(newValue.getOrDefault(entry.getKey(), "")).build()).toList());
        activityUpdates.addAll(newValue.entrySet().stream()
            .filter(entry -> !oldValue.getOrDefault(entry.getKey(), "").equals(entry.getValue()))
            .map(entry -> ActivityUpdate.newBuilder()
                .setFieldName(entry.getKey())
                .setFieldType("string")
                .setOldValue(oldValue.getOrDefault(entry.getKey(), ""))
                .setNewValue(entry.getValue()).build()).toList());
        return activityUpdates;
    }

    private static Object getFieldValue(Object obj, String name) throws IllegalAccessException {
        var clazz = obj.getClass();
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception ignored) {
            }
            clazz = clazz.getSuperclass();
        }
        if (Objects.isNull(field) && name.equals("isActive")) {
            return false;
        }
        field.setAccessible(true);
        return field.get(obj);
    }

    private static String getFieldDataType(Object obj) {
        var className = obj.getClass().getSimpleName();
        if (className.contains("[]")) {
            return className.replace("[]", "-array").toLowerCase();
        }
        return className.toLowerCase();
    }
}
