package com.georgen.melquiades.model.historic;

import com.georgen.melquiades.model.data.DataRoot;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WeekReport extends HistoryReport {
    public ConcurrentMap<Integer, DayReport> days;

    public WeekReport() {
        this.days = new ConcurrentHashMap<>();
    }

    public ConcurrentMap<Integer, DayReport> getDays() {
        return days;
    }

    public void setDays(ConcurrentMap<Integer, DayReport> days) {
        this.days = days;
    }

    @Override
    public void consume(DataRoot data) {
        int day = LocalDate.now().getDayOfWeek().getValue();
        DayReport report = days.get(day);
        if (report == null) report = new DayReport();
        report.consume(data);
        days.put(day, report);
    }

}
