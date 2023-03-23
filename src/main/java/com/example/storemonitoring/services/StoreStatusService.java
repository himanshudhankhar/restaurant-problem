package com.example.storemonitoring.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.StoreStatus;
import com.example.storemonitoring.repository.StoreStatusRepository;

@Component
public class StoreStatusService {
    @Autowired
    StoreStatusRepository repo;

    public StoreStatus addStatus(StoreStatus newStatus) {
        return repo.save(newStatus);
    }

    public StoreStatus parseStatus(List<String> status) throws Exception {
        if (status.size() < 3) {
            throw new Exception("Invalid entry as " + status);
        }
        String timeStamp = status.get(2);
        String parsableTime = convertToParseableTime(timeStamp);
        StoreStatus ans = new StoreStatus(0, status.get(0), status.get(1), LocalDateTime.parse(parsableTime));
        return ans;
    }

    public void saveAllStatuses(List<StoreStatus> records) {
        repo.saveAll(records);
    }

    String convertToParseableTime(String timeStamp) throws Exception {
        String[] times = timeStamp.split(" ");
        if (times.length < 2) {
            throw new Exception("Invalid time stamp " + timeStamp);
        }
        return times[0] + "T" + times[1];
    }

}
