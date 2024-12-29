package com.georgen.melquiades.model.historic;

import com.georgen.melquiades.model.data.DataRoot;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MonthReport extends HistoricReport {
    private ConcurrentMap<Integer, WeekReport> weeks;

    public MonthReport() {
        this.weeks = new ConcurrentHashMap<>();
    }

    public ConcurrentMap<Integer, WeekReport> getWeeks() {
        return weeks;
    }

    public void setWeeks(ConcurrentMap<Integer, WeekReport> weeks) {
        this.weeks = weeks;
    }

    @Override
    public void consume(DataRoot data) {
        int week = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        WeekReport report = weeks.get(week);
        if (report == null) report = new WeekReport();
        report.consume(data);
    }
}
