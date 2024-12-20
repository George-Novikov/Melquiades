package com.georgen.melquiades.model.trackers;

import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * This type of tracker does not interact with
 * */
public class IdleTracker implements Tracker {

    private final UUID uuid;
    private final LocalDateTime start;
    private LocalDateTime finish;
    private long duration = -1;
    private Phase phase = Phase.RUNNING;
    private String cluster;
    private String group;
    private String process;

    public IdleTracker(String process) {
        this(Tracker.DEFAULT_CLUSTER, Tracker.DEFAULT_GROUP, process);
    }

    public IdleTracker(String group, String process) {
        this(Tracker.DEFAULT_CLUSTER, group, process);
    }

    public IdleTracker(Class javaClass, String process){
        this(Tracker.DEFAULT_CLUSTER, javaClass.getSimpleName(), process);
    }

    public IdleTracker(String cluster, String group, String process) {
        this.uuid = UUID.randomUUID();
        this.start = LocalDateTime.now();
        this.cluster = cluster;
        this.group = group;
        this.process = process;
    }

    public UUID getUuid() { return uuid; }

    public LocalDateTime getStart() { return start; }

    public LocalDateTime getFinish() { return finish; }

    public long getDuration() { return duration; }

    public Phase getPhase() { return phase; }

    public String getCluster() { return cluster; }

    public void setCluster(String cluster) { this.cluster = cluster; }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public String getProcess() { return process; }

    public void setProcess(String process) { this.process = process; }

    public Tracker finish(Object... args){
        this.finish = LocalDateTime.now();
        this.phase = Phase.FINISHED;
        this.duration = ChronoUnit.MILLIS.between(this.start, this.finish);
        return this;
    }

    @Override
    public Tracker error(Exception e) {
        return null;
    }

    @Override
    public String toString() { return this.print(); }
}
