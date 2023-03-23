package com.example.storemonitoring.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StoreStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    private String storeId;
    private String status;
    private LocalDateTime timeStampUTC;


    public StoreStatus(Integer Id, String storeId, String status, LocalDateTime timeStampUTC) {
        this.Id = Id;
        this.storeId = storeId;
        this.status = status;
        this.timeStampUTC = timeStampUTC;
    }

    public StoreStatus() {
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimeStampUTC() {
        return this.timeStampUTC;
    }

    public void setTimeStampUTC(LocalDateTime timeStampUTC) {
        this.timeStampUTC = timeStampUTC;
    }
}
