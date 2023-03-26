package com.example.storemonitoring.services;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.storemonitoring.models.Menu;
import com.example.storemonitoring.models.Report;
import com.example.storemonitoring.models.StoreStatus;
import com.example.storemonitoring.models.TimeZone;
import com.example.storemonitoring.models.responses.TriggerReportResponse;
import com.example.storemonitoring.repository.MenuRepository;
import com.example.storemonitoring.repository.ReportRepository;
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

    @Autowired
    ReportRepository reportRepo;

    public Report fetchReport(String reportId) {
        Optional<Report> report = reportRepo.findById(Integer.parseInt(reportId));
        if (report.isPresent()) {
            return report.get();
        }
        return null;
    }

    public TriggerReportResponse generateReport(String restoId) {
        List<Menu> ans = menuRepo.findByStoreId(restoId);
        Map<Integer, ArrayList<Menu>> menuMap = new HashMap<Integer, ArrayList<Menu>>();
        for (int i = 0; i < ans.size(); i++) {
            if (menuMap.get(ans.get(i).getDay()) == null || menuMap.get(ans.get(i).getDay()).size() == 0) {
                menuMap.put(ans.get(i).getDay(), new ArrayList<Menu>());
            }
            menuMap.get(ans.get(i).getDay()).add(ans.get(i));
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
            // for last day uptime calculate uptime of hours and add them
            int lastDayUptime = getLastDayUptime(statusWithInBusinessHours);
            double lastDayUpTimeDouble = lastDayUptime / 60.0;
            System.out.println("last day uptime " + lastDayUpTimeDouble);
            // for last week uptime calculate uptime for each day and add it up.
            int lastWeekUptime = getLastWeekUptime(statusWithInBusinessHours);
            double lastWeekUpTimeDouble = lastWeekUptime / 60.0;
            System.out.println("last week uptime " + lastWeekUpTimeDouble);
            // generate last hour downtime it is equal to 60 - uptime of last hour
            int lastHourDownTime = 60 - lastHourUptime;
            System.out.println("last hour downtime " + lastHourDownTime);
            // last day downtime
            long lastDayDownTime = getLastDayDowntime(statusWithInBusinessHours, lastDayUptime, menuMap);
            double lastDayDownTimeDouble = lastDayDownTime / 60.0;
            System.out.println("last day downtime " + lastDayDownTimeDouble);
            // last week downtime
            long lastWeekDownTime = lastWeekDownTime(statusWithInBusinessHours, lastWeekUptime, menuMap);
            double lastWeekDownTimeDouble = lastWeekDownTime / 60.0;
            System.out.println("last week downtime " + lastWeekDownTimeDouble);
            Report generatedReport = new Report(0, restoId, lastHourUptime,
                    lastDayUpTimeDouble, 
                    lastWeekUpTimeDouble,
                    lastHourDownTime, lastDayDownTimeDouble, lastWeekDownTimeDouble);
            
            Report saved = reportRepo.save(generatedReport);
            TriggerReportResponse resp = new TriggerReportResponse(restoId, saved.getId().toString(), true);
            return resp;
        }
        return new TriggerReportResponse(restoId, null, false);
    }

    long lastWeekDownTime(List<StoreStatus> sortedStatusesDesc,
            int lastWeekUptime, Map<Integer, ArrayList<Menu>> menuMap) {
        // traverse menu map and calculate total menu time and subtract weekuptime from
        // it.
        long weekTotal = 0;
        for (Map.Entry<Integer, ArrayList<Menu>> pair : menuMap.entrySet()) {
            weekTotal += getTotalMenuMinutesFromDay(pair.getValue());
        }
        return weekTotal - lastWeekUptime;
    }

    long getLastDayDowntime(List<StoreStatus> sortedStatusesDesc, int lastDayUptime,
            Map<Integer, ArrayList<Menu>> menuMap) {
        if (sortedStatusesDesc == null || sortedStatusesDesc.size() == 0)
            return 24 * 60; // downtime is in mins
        int dayOfWeek = getIntegerFromWeekDay(sortedStatusesDesc.get(0).getTimeStampUTC().getDayOfWeek().toString());
        List<Menu> menus = menuMap.get(dayOfWeek);
        long total = getTotalMenuMinutesFromDay(menus);
        return total - lastDayUptime;
    }

    long getTotalMenuMinutesFromDay(List<Menu> menus) {
        long total = 0;
        for (int i = 0; i < menus.size(); i++) {
            LocalTime end = menus.get(i).getEndTimeLocal().toLocalTime();
            LocalTime start = menus.get(i).getStartTimeLocal().toLocalTime();
            long mins = start.until(end, ChronoUnit.MINUTES);
            total += mins;
        }
        return total;
    }

    int getLastWeekUptime(List<StoreStatus> sortedStatusesDesc) {
        if (sortedStatusesDesc == null || sortedStatusesDesc.size() == 0) {
            return 0;
        }
        LocalDateTime lastDay = sortedStatusesDesc.get(0).getTimeStampUTC();
        LocalDateTime mondayOfWeek = lastDay.minusDays(getIntegerFromWeekDay(lastDay.getDayOfWeek().toString()));
        mondayOfWeek = mondayOfWeek.minusHours(lastDay.getHour()).minusMinutes(lastDay.getMinute())
                .minusSeconds(lastDay.getSecond()).minusNanos(lastDay.getNano());

        List<Integer> minutes = new ArrayList<>();
        List<Integer> hours = new ArrayList<>();
        List<Integer> days = new ArrayList<>();

        int i = 0;
        while (i < sortedStatusesDesc.size()
                &&
                mondayOfWeek.isBefore(sortedStatusesDesc.get(i).getTimeStampUTC())) {
            minutes.add(sortedStatusesDesc.get(i).getTimeStampUTC().getMinute());
            hours.add(sortedStatusesDesc.get(i).getTimeStampUTC().getHour());
            days.add(getIntegerFromWeekDay(sortedStatusesDesc.get(i++).getTimeStampUTC().getDayOfWeek().toString()));
        }

        i = 0;
        int weekSum = 0;
        while (i < days.size()) {
            int j = i;
            List<Integer> mins = new ArrayList<>();
            List<Integer> hrs = new ArrayList<Integer>();
            while (j < days.size() && days.get(j) == days.get(i)) {
                mins.add(minutes.get(j));
                hrs.add(hours.get(j));
                j++;
            }
            weekSum += sumUptimeOfHours(hrs, mins, 15);
            i = j;
        }
        return weekSum;
    }

    int getLastDayUptime(List<StoreStatus> sortedStatusesDesc) {
        if (sortedStatusesDesc == null || sortedStatusesDesc.size() == 0) {
            return 0;
        }
        StoreStatus last = sortedStatusesDesc.get(0);
        List<Integer> hours = new ArrayList<Integer>();
        List<Integer> minutes = new ArrayList<Integer>();
        LocalDateTime lastTimeStamp = last.getTimeStampUTC();
        LocalDateTime dayStartTime = lastTimeStamp.minusHours(lastTimeStamp.getHour()).minusMinutes(
                lastTimeStamp.getMinute()).minusSeconds(lastTimeStamp.getSecond()).minusNanos(lastTimeStamp.getNano());
        int i = 0;
        while (i < sortedStatusesDesc.size()
                &&
                dayStartTime.isBefore(sortedStatusesDesc.get(i).getTimeStampUTC())) {
            minutes.add(sortedStatusesDesc.get(i).getTimeStampUTC().getMinute());
            hours.add(sortedStatusesDesc.get(i++).getTimeStampUTC().getHour());
        }

        // now we have hours:minutes for the
        // for each hour how many units to uptime was there
        // sum up for every hour and assign it to dayuptime

        return sumUptimeOfHours(hours, minutes, 15);
    }

    int sumUptimeOfHours(List<Integer> hours, List<Integer> minutes, int precision) {
        int i = 0;
        int hoursSum = 0;
        while (i < hours.size()) {
            int j = i;
            List<Integer> mins = new ArrayList<>();
            while (j < hours.size() && hours.get(j) == hours.get(i)) {
                mins.add(minutes.get(j));
                j++;
            }
            hoursSum += getUptimeOfHourFromMinutes(mins, precision);
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

    Boolean withInBusinessHours(StoreStatus timeStamp, Map<Integer, ArrayList<Menu>> menuMap, String timezone) {
        String day = timeStamp.getTimeStampUTC().getDayOfWeek().toString();
        int dayEnum = getIntegerFromWeekDay(day);
        ArrayList<Menu> menuList = menuMap.get(dayEnum);
        if (menuList == null || menuList.size() == 0) {
            // if menu hours for a day doesnot exit consider it 24*7;
            Time start = Time.valueOf("00:00:00");
            Time end = Time.valueOf("23:59:59");
            Menu menu = new Menu(0, "default", dayEnum, start, end);
            menuList = new ArrayList<>();
            menuList.add(menu);
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
        Boolean isTimeStampBetweenIntervals = false;
        for (int i = 0; i < menuList.size(); i++) {
            Menu menu = menuList.get(i);
            Boolean isAfter = tm.isAfter(menu.getStartTimeLocal().toLocalTime())
                    || menu.getStartTimeLocal().toLocalTime().equals(tm);
            Boolean isBefore = tm.isBefore(menu.getEndTimeLocal().toLocalTime())
                    || menu.getStartTimeLocal().toLocalTime().equals(tm);
            isTimeStampBetweenIntervals = isTimeStampBetweenIntervals || (isAfter && isBefore);
        }
        return isTimeStampBetweenIntervals;
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
