package com.georgen.melquiades.core.trackers;

import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.util.UUID;

/** This class is MUCH more expensive because of getStackTrace() method */
public class StackTracker extends Throwable implements Tracker {
    public static final int DEFAULT_STACK_DEPTH = 1;

    private final UUID uuid;
    private final StackTraceElement[] stackTrace;
    private final LocalDateTime start;
    private LocalDateTime finish;
    private long duration;
    private Phase phase;
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

    public LocalDateTime getStart() { return start; }

    public LocalDateTime getFinish() { return finish; }

    public void setFinish(LocalDateTime finish) { this.finish = finish; }

    public long getDuration() { return duration; }

    public void setDuration(long duration) { this.duration = duration; }

    public Phase getPhase() { return phase; }

    public void setPhase(Phase phase) { this.phase = phase; }

    public String getCluster() { return groupName; }

    public void setCluster(String cluster) { this.groupName = cluster; }

    public String getGroup() { return subGroupName; }

    public void setGroup(String group) { this.subGroupName = group; }

    public String getProcess() { return methodName; }

    public void setProcess(String process) { this.methodName = process; }

    @Override
    public StackTraceElement[] getStackTrace() { return stackTrace; }

    @Override
    public String toString() { return this.print(); }

    public static StackTracker start(){ return new StackTracker(); }
}
