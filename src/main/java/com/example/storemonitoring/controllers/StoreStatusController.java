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

import com.example.storemonitoring.models.StoreStatus;
import com.example.storemonitoring.models.responses.AddStatusCSVResponse;
import com.example.storemonitoring.services.StoreStatusService;
import com.opencsv.CSVReader;

@RestController
public class StoreStatusController {
    @Autowired
    StoreStatusService storeStatusService;

    @PostMapping(path = "/add-status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoreStatus> create(@RequestBody StoreStatus newStatus) {
        StoreStatus status = storeStatusService.addStatus(newStatus);
        if (status == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(status, HttpStatus.CREATED);
        }
    }

    @GetMapping(path = "/add-status-from-csv/{csv-path}")
    public @ResponseBody AddStatusCSVResponse addStatusFromCSV(@PathVariable(value = "csv-path") String csvPath) {
        List<StoreStatus> records = new ArrayList<StoreStatus>();
        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath + ".csv"));) {
            String[] values = null;
            values = csvReader.readNext();
            if (values.length != 3) {
                System.out.println("Exeception while parsing headers, not valid headers");
                return new AddStatusCSVResponse(null, "not valid headers", false);
            }

            if (!values[0].equals("store_id")) {
                System.out.println("Exeception while parsing headers, first header is not store_id");
                return new AddStatusCSVResponse(null, "first header is not store_id", false);
            }

            if (!values[1].equals("status")) {
                System.out.println("Exeception while parsing headers, second header is not status");
                return new AddStatusCSVResponse(null, "second header is not status", false);
            }

            if (!values[2].equals("timestamp_utc")) {
                System.out.println("Exeception while parsing headers, third header is not timestamp_utc");
                return new AddStatusCSVResponse(null, "third header is not timestamp_utc", false);
            }

            while ((values = csvReader.readNext()) != null) {
                try {
                    StoreStatus parsedStatus = storeStatusService.parseStatus(Arrays.asList(values));
                    records.add(parsedStatus);
                } catch (Exception e) {
                    System.out.println("Exeception while parsing Status " + e.getLocalizedMessage());
                    return new AddStatusCSVResponse(null, "Exeception while parsing Status " + e.getLocalizedMessage(),
                            false);
                }
            }
            storeStatusService.saveAllStatuses(records);
            return new AddStatusCSVResponse(records.subList(0, 10), "", true);
        } catch (Exception e) {
            System.out.println("Exeception while reading csv " + e.getLocalizedMessage());
            return new AddStatusCSVResponse(null, "Exeception while parsing Menu " + e.getLocalizedMessage(), false);
        }
    }
}
