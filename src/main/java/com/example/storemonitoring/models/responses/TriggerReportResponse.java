package com.example.storemonitoring.models.responses;

public class TriggerReportResponse {
    String storeId;
    String reportId;
    Boolean success;

    public Boolean isSuccess() {
        return this.success;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getReportId() {
        return this.reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public TriggerReportResponse(String storeId, String reportId, Boolean success) {
        this.storeId = storeId;
        this.reportId = reportId;
        this.success = success;
    }

    public TriggerReportResponse() {
    }

}
