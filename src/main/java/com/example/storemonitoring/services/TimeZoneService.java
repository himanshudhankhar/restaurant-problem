package com.example.storemonitoring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.TimeZone;
import com.example.storemonitoring.repository.TimeZoneRepository;

@Component
public class TimeZoneService {
    @Autowired
    TimeZoneRepository timeZoneRepo;

    public TimeZone addRestoTimeZone(TimeZone timezone) {
        return timeZoneRepo.save(timezone);
    }

    public TimeZone parseTimeZone(List<String> input) throws Exception {
        if (input.size() < 2) {
            throw new Exception("Invalid entry as " + input);
        }
        return new TimeZone(0, input.get(0), input.get(1));
    }

    public void saveAllTimeZones(List<TimeZone> records) {
        timeZoneRepo.saveAll(records);
    }

}
