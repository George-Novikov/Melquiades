package com.georgen.melquiades.model.historic;

import com.georgen.melquiades.model.data.DataRoot;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class YearReport extends HistoricReport {
    private ConcurrentMap<Integer, MonthReport> months;

    public YearReport(){
        this.months = new ConcurrentHashMap<>();
    }

    public ConcurrentMap<Integer, MonthReport> getMonths() {
        return months;
    }

    public void setMonths(ConcurrentMap<Integer, MonthReport> months) {
        this.months = months;
    }

    @Override
    public void consume(DataRoot data){
        int month = LocalDate.now().getMonth().getValue();
        MonthReport report = months.get(month);
        if (report == null) report = new MonthReport();
        report.consume(data);
    }
}
