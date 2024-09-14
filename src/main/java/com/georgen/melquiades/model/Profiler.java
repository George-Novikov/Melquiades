package com.georgen.melquiades.model;

import com.georgen.melquiades.api.Operation;
import com.georgen.melquiades.util.OperationExtractor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class Profiler extends Throwable {
    public static final int DEFAULT_STACK_DEPTH = 0;

    private final String uuid;
    private final StackTraceElement[] stackTrace;
    private long start;
    private long finish;
    private long duration;
    private String className;
    private String methodName;

    public Profiler(){
        this(DEFAULT_STACK_DEPTH);
    }

    protected Profiler(int stackDepth) {
        this.start = System.currentTimeMillis();
        this.uuid = UUID.randomUUID().toString();
        this.stackTrace = super.getStackTrace();
        StackTraceElement throwable = this.stackTrace[stackDepth];
        this.className = throwable.getClassName();
        this.methodName = throwable.getMethodName();
    }

    public String getUuid() { return uuid; }

    @Override
    public StackTraceElement[] getStackTrace() { return stackTrace; }

    public long getStart() { return start; }

    public void setStart(long start) { this.start = start; }

    public long getFinish() { return finish; }

    public void setFinish(long finish) { this.finish = finish; }

    public long getDuration() { return duration; }

    public void setDuration(long duration) { this.duration = duration; }

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public String getMethodName() { return methodName; }

    public void setMethodName(String methodName) { this.methodName = methodName; }

    public Profiler finish(){
        this.finish = System.currentTimeMillis();
        this.duration = this.finish - this.start;

        List<Operation> operations = getOperations();


        System.out.println(this);
        return this;
    }

    public List<Operation> getOperations(){
        return Arrays.stream(this.stackTrace)
                .map(OperationExtractor::extract)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "AnalysisReport{" +
                "start=" + start +
                ", finish=" + finish +
                ", duration=" + duration +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
