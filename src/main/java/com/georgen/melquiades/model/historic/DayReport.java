package com.georgen.melquiades.model.historic;

import com.georgen.melquiades.model.data.DataRoot;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;

public class DayReport {
    private ConcurrentMap<Integer, DataRoot> hours;

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
