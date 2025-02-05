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

    public static List<DataRoot> findRange(LocalDateTime start, LocalDateTime finish) throws IOException {
        List<DataRoot> reportList = new ArrayList<>();

        try (BufferReader reader = new BufferReader(Profiler.settings().getLogPath())){
            String stringStart = wrapForSearch(DATE_TIME_FORMATTER.format(start));
            String stringFinish = wrapForSearch(DATE_TIME_FORMATTER.format(finish));

            long startPosition = reader.firstPosition(stringStart);
            long finishPosition = reader.lastPosition(stringFinish);
            if (startPosition == -1 || finishPosition == -1) return reportList;

            List<String> lines = reader.linesBetween(startPosition, finishPosition);
            for (String line : lines) {
                reportList.add(Serializer.deserialize(line, DataRoot.class));
            }
        }

        return reportList;
    }

    public static List<DataRoot> findRange(LocalDateTime start, LocalDateTime finish, int clearance) throws IOException {
        List<DataRoot> reportList = findRange(start, finish);
        if (!reportList.isEmpty()) return reportList;

        List<LocalDateTime> startAlternatives = getTimeVariations(start, clearance, true);
        List<LocalDateTime> finishAlternatives = getTimeVariations(finish, clearance, true);

        for (LocalDateTime altStart : startAlternatives) {
            for (LocalDateTime altFinish : finishAlternatives) {
                reportList = findRange(altStart, altFinish);
                if (!reportList.isEmpty()) return reportList;
            }
        }

        return reportList;
    }

    public static DataRoot findSlice(LocalDateTime time, boolean isBackwards) throws IOException {
        try (BufferReader reader = new BufferReader(Profiler.settings().getLogPath())){
            String timeString = wrapForSearch(DATE_TIME_FORMATTER.format(time));
            long position = isBackwards ? reader.lastPosition(timeString) : reader.firstPosition(timeString);
            if (position == -1) return null;
            String result = reader.readLine(position);
            if (result == null) return null;
            return Serializer.deserialize(result, DataRoot.class);
        }
    }

    public static DataRoot findSlice(LocalDateTime time, boolean isBackwards, int clearance) throws IOException {
        DataRoot result = findSlice(time, isBackwards);
        if (result != null) return result;

        // Since we already searched for exact match, isInclusive = false
        List<LocalDateTime> timeAlternatives = getTimeVariations(time, clearance, false);
        for (LocalDateTime timeString : timeAlternatives) {
            result = findSlice(time, isBackwards);
            if (result != null) return result;
        }
        return result;
    }

    private static List<LocalDateTime> getTimeVariations(LocalDateTime time, int clearance, boolean isInclusive) {
        List<LocalDateTime> alternatives = new ArrayList<>();
        for (int i = 0; i <= clearance; i++) {
            alternatives.add(time.minusSeconds(i));
            alternatives.add(time.plusSeconds(i));
        }
        if (isInclusive) alternatives.add(time);
        return alternatives;
    }

    private static String wrapForSearch(String stringDate){
        return String.format("{\"start\":\"%s", stringDate);
    }
}
