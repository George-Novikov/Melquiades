package com.georgen.melquiades.model.trackers;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Tracker {

    String DEFAULT_CLUSTER = "DEFAULT_CLUSTER";
    String DEFAULT_GROUP = "DEFAULT_GROUP";
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

    Tracker finish(Object... args);

    Tracker error(Exception e);

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

    static Tracker start(String ...args){
        Tracker tracker = null;

        if (args == null || args.length == 0) {
            tracker = new StackTracker();
        }

        if (args.length == 1) {
            tracker = new NamedTracker(DEFAULT_GROUP, args[0]);
        }

        if (tracker == null) {
            tracker = new NamedTracker(args[0], args[1]);
        }

        Profiler.getInstance().process(tracker);

        return tracker;
    }
}
