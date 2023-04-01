package com.leftbin.commons.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(callSuper=false)
public class EventEntity extends BaseEntity {
    @Column(name = "state_cre_by")
    protected String stateCreBy;
    @Column(name = "state_cre_ts")
    protected Timestamp stateCreTs;
    @Column(name = "state_upd_by")
    protected String stateUpdBy;
    @Column(name = "state_upd_ts")
    protected Timestamp stateUpdTs;
    @Column(name = "evt_type")
    protected String evtType;

    public void setWhoColsForMsgCre(String who, Timestamp ts) {
        this.setStateCreBy(who);
        this.setStateCreTs(ts);
        this.setWhoColsForMsgUpd(who, ts);
    }

    public void setWhoColsForMsgUpd(String who, Timestamp ts) {
        this.setStateUpdBy(who);
        this.setStateUpdTs(ts);
    }
}
