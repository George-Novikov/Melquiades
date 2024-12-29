package com.georgen.melquiades.model.historic;

import com.georgen.melquiades.model.data.DataRoot;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DayReport extends HistoryReport {
    private ConcurrentMap<Integer, DataRoot> hours;

    public DayReport(){ hours = new ConcurrentHashMap<>(); }

    public ConcurrentMap<Integer, DataRoot> getHours() {
        return hours;
    }

    public void setHours(ConcurrentMap<Integer, DataRoot> hours) {
        this.hours = hours;
    }

    public void consume(DataRoot data){
        int hour = LocalDateTime.now().getHour();
        DataRoot oldData = hours.get(hour);

        if (oldData == null){
            hours.put(hour, data);
            return;
        }

        oldData.averageWith(data);
        hours.put(hour, oldData);
    }
}
