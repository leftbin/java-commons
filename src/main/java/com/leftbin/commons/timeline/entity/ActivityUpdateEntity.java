package com.leftbin.commons.timeline.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "activity_update")
@Getter
@Setter
@ToString
@TypeDefs({
    @TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
    )
})
public class ActivityUpdateEntity {
    @Id
    @Column(name = "activity_update_id")
    private String activityUpdateId;
    @Column(name = "activity_id")
    private String activityId;
    @Column(name = "field_name")
    private String fieldName;
    @Column(name = "field_type")
    private String fieldType;
    @Column(name = "old_value")
    private String oldValue;
    @Column(name = "new_value")
    private String newValue;
    @Type( type = "string-array")
    @Column(name = "added_values", columnDefinition="text[]")
    private String[] addedValues;
    @Type( type = "string-array")
    @Column(name = "removed_values", columnDefinition="text[]")
    private String[] removedValues;

}
