package com.georgen.melquiades.model.settings;

import com.georgen.melquiades.model.historic.*;

public enum HistoryDepth {
    YEAR(YearReport.class),
    MONTH(MonthReport.class),
    WEEK(WeekReport.class),
    DAY(DayReport.class),
    NONE(null);

    private final Class<? extends HistoryReport> javaClass;

    HistoryDepth(Class<? extends HistoryReport> javaClass) {
        this.javaClass = javaClass;
    }

    public Class<? extends HistoryReport> getJavaClass() {
        return javaClass;
    }
}
