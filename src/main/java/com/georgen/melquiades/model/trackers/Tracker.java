package com.georgen.melquiades.model.trackers;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Tracker {

    String DEFAULT_CLUSTER = "default_cluster";
    String DEFAULT_GROUP = "default_group";
    String KEY_SEPARATOR = "=";
    String VALUE_SEPARATOR = " ";

    UUID getUuid();

    LocalDateTime getStart();

    LocalDateTime getFinish();

    long getDuration();

    Phase getPhase();

    String getCluster();

    void setCluster(String cluster);

    String getGroup();

    void setGroup(String group);

    String getProcess();

    void setProcess(String process);

    default boolean hasCluster(){ return this.getCluster() != null && !this.getCluster().isEmpty(); }

    default boolean hasGroup(){ return this.getGroup() != null && !this.getGroup().isEmpty(); }

    default boolean hasProcess(){ return this.getProcess() != null && !this.getProcess().isEmpty(); }

    Tracker finish(Object... args);

    Tracker error(Exception e);

    default boolean isValid(){
        return this.getCluster() != null && !this.getCluster().isEmpty()
                && this.getGroup() != null && !this.getGroup().isEmpty()
                && this.getProcess() != null && !this.getProcess().isEmpty();
    }

    default String print(){
        return new StringBuilder()
                .append("start").append(KEY_SEPARATOR).append(getStart()).append(VALUE_SEPARATOR)
                .append("finish").append(KEY_SEPARATOR).append(getFinish()).append(VALUE_SEPARATOR)
                .append("duration").append(KEY_SEPARATOR).append(getDuration()).append(VALUE_SEPARATOR)
                .append("status").append(KEY_SEPARATOR).append(getPhase()).append(VALUE_SEPARATOR)
                .append("groupName").append(KEY_SEPARATOR).append(getCluster()).append(VALUE_SEPARATOR)
                .append("methodName").append(KEY_SEPARATOR).append(getProcess()).append(VALUE_SEPARATOR)
                .toString();
    }

    static Tracker start(){
        Tracker tracker = new StackTracker();
        Profiler.getInstance().process(tracker);
        return tracker;
    }

    static Tracker start(String process){
        return start(DEFAULT_CLUSTER, DEFAULT_GROUP, process);
    }

    static Tracker start(String group, String process){
        return start(DEFAULT_CLUSTER, group, process);
    }

    static Tracker start(String cluster, String group, String process) {
        boolean isEnabled = Profiler.isEnabled();
        Tracker tracker = isEnabled ? new NamedTracker(cluster, group, process) : new IdleTracker(cluster, group, process);
        if (isEnabled) Profiler.getInstance().process(tracker);
        return tracker;
    }
}
