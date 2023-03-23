package com.example.storemonitoring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TimeZone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    private String storeId;
    private String timezoneStr;

    public Integer getId() {
        return this.Id;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTimezoneStr() {
        return this.timezoneStr;
    }

    public void setTimezoneStr(String timezoneStr) {
        this.timezoneStr = timezoneStr;
    }

    public TimeZone(Integer Id, String storeId, String timezoneStr) {
        this.Id = Id;
        this.storeId = storeId;
        this.timezoneStr = timezoneStr;
    }

    public TimeZone() {
    }

}
