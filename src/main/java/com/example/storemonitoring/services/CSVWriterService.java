package com.example.storemonitoring.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Report;

@Component
public class CSVWriterService {

    public void writeReportListToCsv(List<Report> reportList, PrintWriter writer) {
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("store_id", "uptime_last_hour", "uptime_last_day", "uptime_last_week",
                    "downtime_last_hour", "downtime_last_day", "downtime_last_week");
            for (Report r : reportList) {
                csvPrinter.printRecord(r.getStoreId(), r.getLastHourUptime(), r.getLastDayUptime(),
                        r.getLastWeekUptime(), r.getLastHourDownTime(), r.getLastDayDownTime(),
                        r.getLastWeekDownTime());
            }
        } catch (IOException e) {
            System.out.println("Error While writing CSV " + e.getLocalizedMessage());
        }
    }
}
