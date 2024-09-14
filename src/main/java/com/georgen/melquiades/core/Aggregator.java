package com.georgen.melquiades.core;

import com.georgen.melquiades.model.Profiler;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Aggregator {

    private static class Holder {
        private static long updateInterval = 1000;
        private static LocalDateTime currentSlice = LocalDateTime.now();
        private static LocalDateTime nextSlice =

        private static final ConcurrentMap<String, ClassSlice> CLASS_SLICE = new ConcurrentHashMap<>();
        private static final ConcurrentMap<String, MethodSlice> METHOD_SLICE = new ConcurrentHashMap<>();
        private static final ConcurrentMap<String, OperationSlice> OPERATION_SLICE = new ConcurrentHashMap<>();
        private static final ConcurrentMap<String, TimeSlice> TIME_SLICE = new ConcurrentHashMap<>();
    }

    public static void register(Profiler profiler){

    }

}
