package com.georgen.melquiades.model.trackers;

import com.georgen.melquiades.api.Operation;
import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Phase;
import com.georgen.melquiades.util.OperationExtractor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/** This class is much more expensive because of getStackTrace() method */
public class StackTracker extends Throwable implements Tracker {
    public static final int DEFAULT_STACK_DEPTH = 0;

    private final UUID uuid;
    private final StackTraceElement[] stackTrace;
    private final LocalDateTime start;
    private LocalDateTime finish;
    private long duration;
    private Phase status;
    private String groupName;
    private String subGroupName;
    private String methodName;

    public StackTracker(){
        this(DEFAULT_STACK_DEPTH);
    }

    protected StackTracker(int stackDepth) {
        this.start = LocalDateTime.now();
        this.uuid = UUID.randomUUID();
        this.stackTrace = super.getStackTrace();
        StackTraceElement throwable = this.stackTrace[stackDepth];
        this.groupName = throwable.getClassName();
        this.methodName = throwable.getMethodName();
    }

    public UUID getUuid() { return uuid; }

    @Override
    public StackTraceElement[] getStackTrace() { return stackTrace; }

    public LocalDateTime getStart() { return start; }

    public LocalDateTime getFinish() { return finish; }

    public long getDuration() { return duration; }

    @Override
    public Phase getPhase() { return status; }

    public String getCluster() { return groupName; }

    public void setCluster(String cluster) { this.groupName = cluster; }

    public String getGroup() { return subGroupName; }

    public void setGroup(String group) { this.subGroupName = group; }

    public String getProcess() { return methodName; }

    public void setProcess(String process) { this.methodName = process; }

    public Tracker finish(Object... args){
        this.finish = LocalDateTime.now();
        this.duration = ChronoUnit.MILLIS.between(this.start, this.finish);

        List<Operation> operations = getOperations();
        Profiler.getInstance().process(this);

        return this;
    }

    @Override
    public Tracker error(Exception e) {
        return null;
    }

    public List<Operation> getOperations(){
        return Arrays.stream(this.stackTrace)
                .map(OperationExtractor::extract)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() { return this.print(); }

    public static StackTracker start(){ return new StackTracker(); }
}
