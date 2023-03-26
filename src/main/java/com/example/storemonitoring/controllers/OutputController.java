package com.example.storemonitoring.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.storemonitoring.models.Report;
import com.example.storemonitoring.models.responses.TriggerReportResponse;
import com.example.storemonitoring.services.CSVWriterService;
import com.example.storemonitoring.services.OutputService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class OutputController {
    @Autowired
    OutputService outputService;

    @Autowired
    CSVWriterService csvWriterService;

    @GetMapping(path = "/trigger-report/{restoId}")
    public @ResponseBody TriggerReportResponse generateReportForResto(@PathVariable(value = "restoId") String restoId) {

        return outputService.generateReport(restoId);
    }

    @GetMapping(path = "/get-report/{reportId}")
    public void getReportInCSV(@PathVariable(value = "reportId") String reportId, HttpServletResponse servletResponse) 
            throws IOException {
        Report report = outputService.fetchReport(reportId);
        List<Report> ReportList = new ArrayList<Report>();
        ReportList.add(report);
        
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"report.csv\"");
        csvWriterService.writeReportListToCsv(ReportList, servletResponse.getWriter());
    }
}
