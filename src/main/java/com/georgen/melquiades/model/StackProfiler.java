package com.georgen.melquiades.model;

import com.georgen.melquiades.api.Operation;
import com.georgen.melquiades.core.ProfilerRegistry;
import com.georgen.melquiades.util.OperationExtractor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/** This class is much more expensive because of getStackTrace() method */
public class StackProfiler extends Throwable implements Profiler {
    public static final int DEFAULT_STACK_DEPTH = 0;

    private final String uuid;
    private final StackTraceElement[] stackTrace;
    private LocalDateTime start;
    private LocalDateTime finish;
    private long duration;
    private String className;
    private String methodName;

    public StackProfiler(){
        this(DEFAULT_STACK_DEPTH);
    }

    protected StackProfiler(int stackDepth) {
        this.start = LocalDateTime.now();
        this.uuid = UUID.randomUUID().toString();
        this.stackTrace = super.getStackTrace();
        StackTraceElement throwable = this.stackTrace[stackDepth];
        this.className = throwable.getClassName();
        this.methodName = throwable.getMethodName();
    }

    public String getUuid() { return uuid; }

    @Override
    public StackTraceElement[] getStackTrace() { return stackTrace; }

    public LocalDateTime getStart() { return start; }

    public LocalDateTime getFinish() { return finish; }

    public long getDuration() { return duration; }

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public String getMethodName() { return methodName; }

    public void setMethodName(String methodName) { this.methodName = methodName; }

    public StackProfiler finish(Object... args){
        this.finish = LocalDateTime.now();
        this.duration = ChronoUnit.MILLIS.between(this.start, this.finish);

        List<Operation> operations = getOperations();
        ProfilerRegistry.process(this);

        return this;
    }

    @Override
    public Profiler error(Exception e) {
        return null;
    }

    public List<Operation> getOperations(){
        return Arrays.stream(this.stackTrace)
                .map(OperationExtractor::extract)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "StackProfiler{" +
                "uuid='" + uuid + '\'' +
                ", start=" + start +
                ", finish=" + finish +
                ", duration=" + duration +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }

    public static StackProfiler start(){ return new StackProfiler(); }
}
