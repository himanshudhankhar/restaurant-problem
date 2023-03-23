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

import com.example.storemonitoring.models.Menu;
import com.example.storemonitoring.models.responses.AddMenuCSVResponse;
import com.example.storemonitoring.services.MenuService;
import com.opencsv.CSVReader;

@RestController
public class MenuController {
    @Autowired
    MenuService menuService;

    @PostMapping(path = "/add-menu", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> create(@RequestBody Menu newMenu) {
        Menu menu = menuService.addMenu(newMenu);
        if (menu == null) {
            return new ResponseEntity<>(menu, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(menu, HttpStatus.CREATED);
        }
    }

    @GetMapping(path="/add-menu-from-csv/{csv-path}")
    public @ResponseBody AddMenuCSVResponse addMenuFromCSV(@PathVariable(value = "csv-path") String csvPath) {
        List<Menu> records = new ArrayList<Menu>();
        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath + ".csv"));) {
            String[] values = null;
            values = csvReader.readNext();
            if (values.length != 4) {
                System.out.println("Exeception while parsing headers, not valid headers");
                return new AddMenuCSVResponse(null, "not valid headers", false);
            }

            if (!values[0].equals("store_id")) {
                System.out.println("Exeception while parsing headers, first header is not store_id");
                return new AddMenuCSVResponse(null, "first header is not store_id", false);
            }

            if (!values[1].equals("day")) {
                System.out.println("Exeception while parsing headers, second header is not day");
                return new AddMenuCSVResponse(null, "second header is not day", false);
            }

            if (!values[2].equals("start_time_local")) {
                System.out.println("Exeception while parsing headers, third header is not start_time_local");
                return new AddMenuCSVResponse(null, "third header is not start_time_local", false);
            }

            if (!values[3].equals("end_time_local")) {
                System.out.println("Exeception while parsing headers, fourth header is not end_time_local");
                return new AddMenuCSVResponse(null, "fourth header is end_time_local", false);
            }

            while ((values = csvReader.readNext()) != null) {
                try {
                    Menu parsedMenu = menuService.parseMenu(Arrays.asList(values));
                    records.add(parsedMenu);
                } catch (Exception e) {
                    System.out.println("Exeception while parsing Menu " + e.getLocalizedMessage());
                    return new AddMenuCSVResponse(null, "Exeception while parsing Menu " + e.getLocalizedMessage(), false);
                }
            }
            menuService.saveAllMenus(records);
            return new AddMenuCSVResponse(records.subList(0, 10), "", true);
        } catch (Exception e) {
            System.out.println("Exeception while reading csv " + e.getLocalizedMessage());
            return new AddMenuCSVResponse(null,"Exeception while parsing Menu " + e.getLocalizedMessage(), false);
        }
    }
}
