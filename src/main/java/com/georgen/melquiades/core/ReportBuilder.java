package com.georgen.melquiades.core;

import com.georgen.melquiades.io.BufferReader;
import com.georgen.melquiades.model.data.DataRoot;
import com.georgen.melquiades.util.Serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportBuilder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static List<DataRoot> getReport(LocalDateTime start, LocalDateTime finish) throws IOException {
        List<DataRoot> reportList = new ArrayList<>();

        try (BufferReader reader = new BufferReader(Profiler.settings().getLogPath())){
            String stringStart = wrapForSearch(DATE_TIME_FORMATTER.format(start));
            String stringFinish = wrapForSearch(DATE_TIME_FORMATTER.format(finish));

            long startPosition = reader.firstPosition(stringStart);
            long finishPosition = reader.lastPosition(stringFinish);

            List<String> lines = reader.linesBetween(startPosition, finishPosition);
            for (String line : lines) {
                reportList.add(Serializer.deserialize(line, DataRoot.class));
            }
        }

        return reportList;
    }

    private static String wrapForSearch(String stringDate){
        return String.format("{\"start\":\"%s", stringDate);
    }
}
