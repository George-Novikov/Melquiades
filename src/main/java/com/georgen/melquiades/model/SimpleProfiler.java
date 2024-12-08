package com.georgen.melquiades.model;

import com.georgen.melquiades.core.ProfilerRegistry;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class SimpleProfiler implements Profiler {

    private final String uuid;
    private final LocalDateTime start;
    private LocalDateTime finish;
    private long duration;
    private String className;
    private String methodName;

    private SimpleProfiler() {
        this.uuid = UUID.randomUUID().toString();
        this.start = LocalDateTime.now();
    }

    public SimpleProfiler(String className, String methodName) {
        this();
        this.className = className;
        this.methodName = methodName;
    }

    public String getUuid() { return uuid; }

    public LocalDateTime getStart() { return start; }

    public LocalDateTime getFinish() { return finish; }

    public long getDuration() { return duration; }

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public String getMethodName() { return methodName; }

    public void setMethodName(String methodName) { this.methodName = methodName; }

    public static SimpleProfiler start(String className, String methodName){ return new SimpleProfiler(className, methodName); }

    public Profiler finish(Object... args){
        this.finish = LocalDateTime.now();
        this.duration = ChronoUnit.MILLIS.between(this.start, this.finish);
        ProfilerRegistry.process(this);
        return this;
    }

    @Override
    public Profiler error(Exception e) {
        return null;
    }

    @Override
    public String toString() {
        return "Profiler{" +
                "uuid='" + uuid + '\'' +
                ", start=" + start +
                ", finish=" + finish +
                ", duration=" + duration +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
