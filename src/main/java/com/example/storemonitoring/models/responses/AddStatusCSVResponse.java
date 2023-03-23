package com.example.storemonitoring.models.responses;

import com.example.storemonitoring.models.StoreStatus;

public class AddStatusCSVResponse {
    Iterable<StoreStatus> statuses;
    String error;
    Boolean success;


    public AddStatusCSVResponse(Iterable<StoreStatus> statuses, String error, Boolean success) {
        this.statuses = statuses;
        this.error = error;
        this.success = success;
    }

    public Iterable<StoreStatus> getStatuses() {
        return this.statuses;
    }

    public void setStatuses(Iterable<StoreStatus> statuses) {
        this.statuses = statuses;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean isSuccess() {
        return this.success;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public AddStatusCSVResponse() {
    }

}
