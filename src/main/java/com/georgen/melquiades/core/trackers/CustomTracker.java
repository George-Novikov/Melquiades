package com.georgen.melquiades.core.trackers;

public class CustomTracker extends Tracker {
    public CustomTracker(String group, String process) {
        super("CUSTOM_CLUSTER", group, process);
    }
    public static Tracker start(String group, String process){
        Tracker tracker = new CustomTracker(group, process);
        tracker.register();
        return tracker;
    }
}
