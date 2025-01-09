package com.georgen.melquiades.core.trackers;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public abstract class Tracker {

    public static final String DEFAULT_CLUSTER = "DEFAULT_CLUSTER";
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    public static final String KEY_SEPARATOR = "=";
    public static final String VALUE_SEPARATOR = " ";

    private static final int DEFAULT_STACK_DEPTH = 1;

    private final UUID uuid;
    private final LocalDateTime start;
    private LocalDateTime finish;
    private long duration = -1;
    private Phase phase = Phase.RUNNING;
    private String cluster;
    private String group;
    private String process;

    public Tracker(String process) {
        this(DEFAULT_CLUSTER, DEFAULT_GROUP, process);
    }

    public Tracker(String group, String process) {
        this(DEFAULT_CLUSTER, group, process);
    }

    public Tracker(Class javaClass, String process){
        this(DEFAULT_CLUSTER, javaClass.getSimpleName(), process);
    }

    public Tracker(String cluster, String group, String process) {
        this.uuid = UUID.randomUUID();
        this.start = LocalDateTime.now();
        this.cluster = cluster;
        this.group = group;
        this.process = process;
    }

    public UUID getUuid() { return uuid; }

    public LocalDateTime getStart() { return start; }

    public LocalDateTime getFinish() { return finish; }

    public void setFinish(LocalDateTime finish) { this.finish = finish; }

    public long getDuration() { return duration; }

    public void setDuration(long duration) { this.duration = duration; }

    public Phase getPhase() { return phase; }

    public void setPhase(Phase phase) { this.phase = phase; }

    public String getCluster() { return cluster; }

    public void setCluster(String cluster) { this.cluster = cluster; }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public String getProcess() { return process; }

    public void setProcess(String process) { this.process = process; }

    public boolean hasPhase(){ return getPhase() != null; }

    public boolean hasCluster(){ return this.getCluster() != null && !this.getCluster().isEmpty(); }

    public boolean hasGroup(){ return this.getGroup() != null && !this.getGroup().isEmpty(); }

    public boolean hasProcess(){ return this.getProcess() != null && !this.getProcess().isEmpty(); }

    public Tracker finish(Object... args){
        this.setFinish(LocalDateTime.now());
        this.setPhase(Phase.FINISHED);
        this.setDuration(ChronoUnit.MILLIS.between(this.getStart(), this.getFinish()));
        Profiler.getInstance().process(this);
        return this;
    }

    public Tracker error(Exception e) {
        this.setPhase(Phase.ERROR);
        Profiler.getInstance().process(this);
        return this;
    }

    public boolean isValid(){
        return this.getCluster() != null && !this.getCluster().isEmpty()
                && this.getGroup() != null && !this.getGroup().isEmpty()
                && this.getProcess() != null && !this.getProcess().isEmpty();
    }

    public String print(){
        return new StringBuilder()
                .append("start").append(KEY_SEPARATOR).append(getStart()).append(VALUE_SEPARATOR)
                .append("finish").append(KEY_SEPARATOR).append(getFinish()).append(VALUE_SEPARATOR)
                .append("duration").append(KEY_SEPARATOR).append(getDuration()).append(VALUE_SEPARATOR)
                .append("phase").append(KEY_SEPARATOR).append(getPhase()).append(VALUE_SEPARATOR)
                .append("cluster").append(KEY_SEPARATOR).append(getCluster()).append(VALUE_SEPARATOR)
                .append("group").append(KEY_SEPARATOR).append(getGroup()).append(VALUE_SEPARATOR)
                .append("process").append(KEY_SEPARATOR).append(getProcess()).append(VALUE_SEPARATOR)
                .toString();
    }

    @Override
    public String toString(){ return this.print(); }

    public Tracker register(){
        if (Profiler.isEnabled()){
            Profiler.getInstance().process(this);
        }
        return this;
    }

    /** This call is MUCH more expensive because of getStackTrace() method */
    public static Tracker start() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement element = stackTrace[DEFAULT_STACK_DEPTH];
        Tracker tracker = new NamedTracker(Tracker.DEFAULT_CLUSTER, element.getClassName(), element.getMethodName());
        Profiler.getInstance().process(tracker);
        return tracker;
    }

    public static Tracker start(String process){
        return start(DEFAULT_CLUSTER, DEFAULT_GROUP, process);
    }

    public static Tracker start(String group, String process){
        return start(DEFAULT_CLUSTER, group, process);
    }

    public static Tracker start(String cluster, String group, String process) {
        Tracker tracker = Profiler.isEnabled() ? new NamedTracker(cluster, group, process) : new IdleTracker(cluster, group, process);
        tracker.register();
        return tracker;
    }
}
