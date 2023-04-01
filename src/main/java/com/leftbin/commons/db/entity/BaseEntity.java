package com.leftbin.commons.db.entity;

import com.leftbin.commons.time.util.TimestampUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Column(name = "cre_by")
    protected String creBy;
    @Column(name = "cre_ts")
    protected Timestamp creTs;
    @Column(name = "upd_by")
    protected String updBy;
    @Column(name = "upd_ts")
    protected Timestamp updTs;

    public void setWhoColsForCre(String who) {
        this.setCreBy(who);
        this.setCreTs(TimestampUtil.getCurrentTs());
        this.setWhoColsForUpd(who);
    }

    public void setWhoColsForUpd(String who) {
        this.setUpdBy(who);
        this.setUpdTs(TimestampUtil.getCurrentTs());
    }
}
