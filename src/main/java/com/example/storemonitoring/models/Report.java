package com.example.storemonitoring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    String storeId;
    Integer lastHourUptime;
    Double lastDayUptime;
    Double lastWeekUptime;
    Integer lastHourDownTime;
    Double lastDayDownTime;
    Double lastWeekDownTime;


    public Report(Integer Id, String storeId, Integer lastHourUptime, Double lastDayUptime, Double lastWeekUptime, Integer lastHourDownTime, Double lastDayDownTime, Double lastWeekDownTime) {
        this.Id = Id;
        this.storeId = storeId;
        this.lastHourUptime = lastHourUptime;
        this.lastDayUptime = lastDayUptime;
        this.lastWeekUptime = lastWeekUptime;
        this.lastHourDownTime = lastHourDownTime;
        this.lastDayDownTime = lastDayDownTime;
        this.lastWeekDownTime = lastWeekDownTime;
    }

    public Report() {
    }

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

    public Integer getLastHourUptime() {
        return this.lastHourUptime;
    }

    public void setLastHourUptime(Integer lastHourUptime) {
        this.lastHourUptime = lastHourUptime;
    }

    public Double getLastDayUptime() {
        return this.lastDayUptime;
    }

    public void setLastDayUptime(Double lastDayUptime) {
        this.lastDayUptime = lastDayUptime;
    }

    public Double getLastWeekUptime() {
        return this.lastWeekUptime;
    }

    public void setLastWeekUptime(Double lastWeekUptime) {
        this.lastWeekUptime = lastWeekUptime;
    }

    public Integer getLastHourDownTime() {
        return this.lastHourDownTime;
    }

    public void setLastHourDownTime(Integer lastHourDownTime) {
        this.lastHourDownTime = lastHourDownTime;
    }

    public Double getLastDayDownTime() {
        return this.lastDayDownTime;
    }

    public void setLastDayDownTime(Double lastDayDownTime) {
        this.lastDayDownTime = lastDayDownTime;
    }

    public Double getLastWeekDownTime() {
        return this.lastWeekDownTime;
    }

    public void setLastWeekDownTime(Double lastWeekDownTime) {
        this.lastWeekDownTime = lastWeekDownTime;
    }

}