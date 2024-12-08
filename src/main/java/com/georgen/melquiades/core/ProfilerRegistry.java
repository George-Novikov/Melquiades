package com.georgen.melquiades.core;

import com.georgen.melquiades.io.BufferedAppender;
import com.georgen.melquiades.model.Profiler;
import com.georgen.melquiades.model.ProfilerGroup;

import java.util.HashMap;
import java.util.Map;

public class ProfilerRegistry {

    private static final Map<String, ProfilerGroup> GROUPS = new HashMap<String, ProfilerGroup>();

    public static void process(Profiler profiler){
        try {
            BufferedAppender appender = new BufferedAppender("profiler.log", System.out::println);
            appender.append(profiler.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
