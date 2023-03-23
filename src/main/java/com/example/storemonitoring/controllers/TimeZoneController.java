package com.example.storemonitoring.controllers;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.storemonitoring.models.TimeZone;
import com.example.storemonitoring.models.responses.AddTimeZoneCSVResponse;
import com.example.storemonitoring.services.TimeZoneService;
import com.opencsv.CSVReader;

@RestController
public class TimeZoneController {
    @Autowired
    TimeZoneService timezoneService;

    @PostMapping(path = "/add-restaurant-timezone", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeZone> addRestoTimeZone(@RequestBody TimeZone timezone) {
        TimeZone timezoneStored = timezoneService.addRestoTimeZone(timezone);
        if (timezoneStored == null) {
            return new ResponseEntity<>(timezoneStored, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(timezoneStored, HttpStatus.CREATED);
        }
    }

    @GetMapping(path = "/add-timezone-from-csv/{csv-path}")
    public @ResponseBody AddTimeZoneCSVResponse addTimeZoneFromCSV(@PathVariable(value = "csv-path") String csvPath) {
        List<TimeZone> records = new ArrayList<TimeZone>();
        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath + ".csv"));) {
            String[] values = null;
            values = csvReader.readNext();
            if (values.length != 2) {
                System.out.println("Exeception while parsing headers, not valid headers");
                return new AddTimeZoneCSVResponse(null, "not valid headers", false);
            }
            if (!values[0].equals("store_id")) {
                System.out.println("Exeception while parsing headers, first header is not store_id");
                return new AddTimeZoneCSVResponse(null, "first header is not store_id", false);
            }
            if (!values[1].equals("timezone_str")) {
                System.out.println("Exeception while parsing headers, second header is not timezone_str");
                return new AddTimeZoneCSVResponse(null, "second header is not timezone_str", false);
            }

            while ((values = csvReader.readNext()) != null) {
                try {
                    TimeZone parsedTimeZone = timezoneService.parseTimeZone(Arrays.asList(values));
                    records.add(parsedTimeZone);
                } catch (Exception e) {
                    System.out.println("Exeception while parsing timezone " + e.getLocalizedMessage());
                    return new AddTimeZoneCSVResponse(null,
                            "Exeception while parsing timezone " + e.getLocalizedMessage(), false);
                }
            }
            timezoneService.saveAllTimeZones(records);
            return new AddTimeZoneCSVResponse(records.subList(0, 10), "", true);
        } catch (Exception e) {
            System.out.println("Exeception while reading csv " + e.getLocalizedMessage());
            return new AddTimeZoneCSVResponse(null, "Exeception while parsing Menu " + e.getLocalizedMessage(), false);
        }
    }
}
