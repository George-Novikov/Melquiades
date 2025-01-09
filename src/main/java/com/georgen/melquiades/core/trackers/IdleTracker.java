package com.georgen.melquiades.core.trackers;

import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * This type of tracker does not interact with Profiler at all
 * */
public class IdleTracker extends Tracker {

    public IdleTracker(String process) {
        super(process);
    }

    public IdleTracker(String group, String process) {
        super(group, process);
    }

    public IdleTracker(String cluster, String group, String process) {
        super(cluster, group, process);
    }

    /** Please note that there's no Profiler call — thus it won't be automatically processed */
    @Override
    public Tracker finish(Object... args){
        this.setFinish(LocalDateTime.now());
        this.setPhase(Phase.FINISHED);
        this.setDuration(ChronoUnit.MILLIS.between(this.getStart(), this.getFinish()));
        return this;
    }

    /** Please note that there's no Profiler call — thus it won't be automatically processed */
    @Override
    public Tracker error(Exception e) {
        this.setPhase(Phase.ERROR);
        return this;
    }
}
