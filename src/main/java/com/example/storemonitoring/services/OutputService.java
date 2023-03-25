package com.example.storemonitoring.services;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Menu;
import com.example.storemonitoring.models.StoreStatus;
import com.example.storemonitoring.models.TimeZone;
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

            // we need one weeks data
            LocalDateTime weekStartDate = latestTimeStamp.get(0).getTimeStampUTC().minusDays(7);

            // find time stamps between the dates
            List<StoreStatus> timeStampsBetweenDates = statusRepo.findAllByTimeStampUTCBetweenAndStoreId(weekStartDate,
                    latestTimeStamp.get(0).getTimeStampUTC(), restoId);

            // find timezone for resto
            String timeZone = getTimeZoneForResto(restoId);

            // now remove those which are not inside business hours.
            List<StoreStatus> statusWithInBusinessHours = new ArrayList<>();
            for (int i = 0; i < timeStampsBetweenDates.size(); i++) {
                if (withInBusinessHours(timeStampsBetweenDates.get(i), menuMap, timeZone)) {
                    statusWithInBusinessHours.add(timeStampsBetweenDates.get(i));
                }
            }
            // sort the statuses by timestamp in decreasing order so that we can find last
            // hour, lastday, last week
            statusWithInBusinessHours.sort((e2, e1) -> e1.getTimeStampUTC().compareTo(e2.getTimeStampUTC()));
            // now from these statuses generate the report like week, day, hourly data
            // uptime and down time.
            // first go for last hour uptime
            int lastHourUptime = getLastHourUptime(statusWithInBusinessHours);
            System.out.println("last hour uptime " + lastHourUptime);
            // for day uptime divide it into slots of 15 min and see how many slots were
            // active
            int lastDayUptime = getLastDayUptime(statusWithInBusinessHours);
            System.out.println("last day uptime " + lastDayUptime);
        }
        return null;
    }

    int getLastDayUptime(List<StoreStatus> sortedStatusesDesc) {
        if (sortedStatusesDesc == null || sortedStatusesDesc.size() == 0) {
            return 0;
        }
        StoreStatus last = sortedStatusesDesc.get(0);
        List<Integer> hours = new ArrayList<Integer>();
        List<Integer> minutes = new ArrayList<Integer>();
        hours.add(last.getTimeStampUTC().getHour());
        minutes.add(last.getTimeStampUTC().getMinute());
        LocalDateTime lastTimeStamp = last.getTimeStampUTC();
        LocalDateTime dayStartTime = lastTimeStamp.minusHours(lastTimeStamp.getHour()).minusMinutes(
                lastTimeStamp.getMinute()).minusSeconds(lastTimeStamp.getSecond());
        int i = 1;
        while (i < sortedStatusesDesc.size()
                &&
                dayStartTime.isBefore(sortedStatusesDesc.get(i).getTimeStampUTC())) {
            minutes.add(sortedStatusesDesc.get(i).getTimeStampUTC().getMinute());
            hours.add(sortedStatusesDesc.get(i++).getTimeStampUTC().getHour());
        }

        // now we have hours:minutes for the
        // for each hour how many units to uptime was there
        // sum up for every hour and assign it to dayuptime
        i = 0;
        int hoursSum = 0;
        while (i < hours.size()) {
            int j = i;
            List<Integer> mins = new ArrayList<>();
            while (j < hours.size() && hours.get(j) == hours.get(i)) {
                mins.add(minutes.get(j));
                j++;
            }
            hoursSum += getUptimeOfHourFromMinutes(mins, 15);
            i = j;
        }

        return hoursSum;
    }

    int getLastHourUptime(List<StoreStatus> sortedStatusesDesc) {
        if (sortedStatusesDesc == null || sortedStatusesDesc.size() == 0) {
            return 0;
        }

        StoreStatus last = sortedStatusesDesc.get(0);
        List<Integer> minutes = new ArrayList<Integer>();
        int i = 1;
        minutes.add(last.getTimeStampUTC().getMinute());
        while (i < sortedStatusesDesc.size()
                && last.getTimeStampUTC().minusHours(1).isBefore(sortedStatusesDesc.get(i).getTimeStampUTC())) {
            minutes.add(sortedStatusesDesc.get(i++).getTimeStampUTC().getMinute());
        }
        return getUptimeOfHourFromMinutes(minutes, 15);
    }

    int getUptimeOfHourFromMinutes(List<Integer> minutes, int precision) {
        int slots = 60 / precision;
        List<Integer> slotsArray = new ArrayList<>();
        List<Integer> slotsFound = new ArrayList<>();
        int i = 0;
        for (i = 0; i < slots; i++) {
            slotsArray.add(i * precision);
            slotsFound.add(0);
        }
        for (i = 0; i < minutes.size(); i++) {
            int pos = findPositionInSlots(slotsArray, minutes.get(i));
            slotsFound.set(pos, 1);
        }
        System.out.println(slotsArray.toString());
        System.out.println(slotsFound.toString());
        int totalSlotsActive = 0;
        for (i = 0; i < slotsFound.size(); i++) {
            totalSlotsActive += slotsFound.get(i);
        }
        return totalSlotsActive * precision;
    }

    int findPositionInSlots(List<Integer> slots, Integer minute) {
        for (int i = 0; i < slots.size() - 1; i++) {
            if (slots.get(i) <= minute && slots.get(i + 1) > minute) {
                return i;
            }
        }
        return slots.size() - 1;
    }

    Boolean withInBusinessHours(StoreStatus timeStamp, Map<Integer, Menu> menuMap, String timezone) {
        String day = timeStamp.getTimeStampUTC().getDayOfWeek().toString();
        int dayEnum = getIntegerFromWeekDay(day);
        Menu menu = menuMap.get(dayEnum);
        if (menu == null) {
            // if menu hours for a day doesnot exit consider it 24*7;
            Time start = Time.valueOf("00:00:00");
            Time end = Time.valueOf("23:59:59");
            menu = new Menu(0, "default", dayEnum, start, end);
        }

        // convert localdatetime to timezoned time;
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime zoned = timeStamp.getTimeStampUTC().atZone(zone);
        String parseAbleTime = zoned.toInstant().toString();
        // parse the converted timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        LocalDateTime dateTime = LocalDateTime.parse(parseAbleTime, formatter);
        // extract Time from zone converted timestamp
        LocalTime tm = dateTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS);
        // check if converted timestamp is between the opening and closing hours;
        Boolean isAfter = tm.isAfter(menu.getStartTimeLocal().toLocalTime())
                || menu.getStartTimeLocal().toLocalTime().equals(tm);
        Boolean isBefore = tm.isBefore(menu.getEndTimeLocal().toLocalTime())
                || menu.getStartTimeLocal().toLocalTime().equals(tm);
        return isAfter && isBefore;
    }

    String getTimeZoneForResto(String restoId) {
        TimeZone timeZone = timezoneRepo.findByStoreId(restoId);
        if (timeZone != null) {
            return timeZone.getTimezoneStr();
        }
        return "America/Chicago";
    }

    int getIntegerFromWeekDay(String day) {
        if (day.equals("MONDAY"))
            return 0;
        if (day.equals("TUESDAY"))
            return 1;
        if (day.equals("WEDNESDAY"))
            return 2;
        if (day.equals("THURSDAY"))
            return 3;
        if (day.equals("FRIDAY"))
            return 4;
        if (day.equals("SATURDAY"))
            return 5;
        return 6;
    }

}
