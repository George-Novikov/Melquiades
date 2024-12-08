package com.georgen.melquiades.model;

import java.time.LocalDateTime;

public interface Profiler {

    String DEFAULT_GROUP = "DEFAULT_PROFILER_GROUP";

    String getUuid();

    LocalDateTime getStart();

    LocalDateTime getFinish();

    long getDuration();

    String getClassName();

    void setClassName(String className);

    String getMethodName();

    void setMethodName(String methodName);

    Profiler finish(Object... args);

    Profiler error(Exception e);

    static Profiler start(String ...args){
        if (args == null || args.length == 0) {
            return new StackProfiler();
        }

        if (args.length == 1) {
            return new SimpleProfiler(DEFAULT_GROUP, args[0]);
        }

        return new SimpleProfiler(args[0], args[1]);
    }
}
