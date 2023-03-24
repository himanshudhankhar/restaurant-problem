package com.example.storemonitoring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.storemonitoring.models.responses.TriggerReportResponse;
import com.example.storemonitoring.services.OutputService;

@RestController
public class OutputController {
    @Autowired
    OutputService outputService;
    
    @GetMapping(path = "/trigger-report/{restoId}")
    public @ResponseBody TriggerReportResponse generateReportForResto(@PathVariable(value = "restoId") String restoId) {

        return outputService.generateReport(restoId);
    }
}
