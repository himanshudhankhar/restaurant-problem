package com.example.storemonitoring.models.responses;

import com.example.storemonitoring.models.TimeZone;

public class AddTimeZoneCSVResponse {
    Iterable<TimeZone> timezones;
    String error;
    Boolean success;


    public AddTimeZoneCSVResponse(Iterable<TimeZone> timezones, String error, Boolean success) {
        this.timezones = timezones;
        this.error = error;
        this.success = success;
    }

    public Iterable<TimeZone> getTimezones() {
        return this.timezones;
    }

    public void setTimezones(Iterable<TimeZone> timezones) {
        this.timezones = timezones;
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

    public AddTimeZoneCSVResponse() {
    }

}
