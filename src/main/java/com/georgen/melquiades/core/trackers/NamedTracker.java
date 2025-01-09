package com.georgen.melquiades.core.trackers;

import com.georgen.melquiades.model.Phase;

import java.time.LocalDateTime;
import java.util.UUID;

public class NamedTracker extends Tracker {

    public NamedTracker(String process) {
        super(process);
    }

    public NamedTracker(String group, String process) {
        super(group, process);
    }

    public NamedTracker(String cluster, String group, String process) {
        super(cluster, group, process);
    }
}
