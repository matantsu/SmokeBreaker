package com.smokebreaker.www.bl.models;

import java.util.Calendar;
import java.util.Date;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

@Table("smokes")
public class Smoke extends Model{
    @Column("date")
    public Long date;
    @Column("timestamp")
    public Long timestamp;

    public Smoke() {
        setTimestamp(new Date().getTime());
    }

    public Smoke(long timestamp) {
        setTimestamp(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        date = calendar.getTime().getTime();
    }

    public Long getDate() {
        return date;
    }
}
