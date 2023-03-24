package com.example.storemonitoring.models.responses;

public class TriggerReportResponse {
    String storeId;
    String reportId;

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

    public TriggerReportResponse(String storeId, String reportId) {
        this.storeId = storeId;
        this.reportId = reportId;
    }

    public TriggerReportResponse() {
    }

}
