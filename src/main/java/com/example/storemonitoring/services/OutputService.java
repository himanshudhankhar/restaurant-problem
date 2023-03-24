package com.example.storemonitoring.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Menu;
import com.example.storemonitoring.models.StoreStatus;
import com.example.storemonitoring.models.responses.TriggerReportResponse;
import com.example.storemonitoring.repository.MenuRepository;
import com.example.storemonitoring.repository.StoreStatusRepository;
import com.example.storemonitoring.repository.TimeZoneRepository;

@Component
public class OutputService {
    @Autowired
    MenuRepository menuRepo;

    @Autowired
    StoreStatusRepository statusRepo;

    @Autowired
    TimeZoneRepository timezoneRepo;

    public TriggerReportResponse generateReport(String restoId) {
        List<Menu> ans = menuRepo.findByStoreId(restoId);
        Map<Integer, Menu> menuMap = new HashMap<Integer, Menu>();
        for (int i = 0; i < ans.size(); i++) {
            menuMap.put(ans.get(i).getDay(), ans.get(i));
        }

        List<StoreStatus> latestTimeStamp = statusRepo.findFirstByStoreIdOrderByTimeStampUTCDesc(restoId);
        if (latestTimeStamp.size() != 0) {
            System.out.println(latestTimeStamp.get(0).getTimeStampUTC().toString());
            LocalDateTime weekStartDate = latestTimeStamp.get(0).getTimeStampUTC().minusDays(7);
            List<StoreStatus> timeStampsBetweenDates = statusRepo.findAllByTimeStampUTCBetweenAndStoreId(weekStartDate,latestTimeStamp.get(0).getTimeStampUTC(), restoId);
            System.out.println(timeStampsBetweenDates.size());
        }
        return null;
    }

}
