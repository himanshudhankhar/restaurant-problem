package com.example.storemonitoring.models;

import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    private String storeId;
    private Integer day;
    private Time startTimeLocal;
    private Time endTimeLocal;

    public Menu(Integer Id, String storeId, Integer day, Time startTimeLocal, Time endTimeLocal) {
        this.Id = Id;
        this.storeId = storeId;
        this.day = day;
        this.startTimeLocal = startTimeLocal;
        this.endTimeLocal = endTimeLocal;
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

    public Integer getDay() {
        return this.day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Time getStartTimeLocal() {
        return this.startTimeLocal;
    }

    public void setStartTimeLocal(Time startTimeLocal) {
        this.startTimeLocal = startTimeLocal;
    }

    public Time getEndTimeLocal() {
        return this.endTimeLocal;
    }

    public void setEndTimeLocal(Time endTimeLocal) {
        this.endTimeLocal = endTimeLocal;
    }

    public Menu() {
    }

}
