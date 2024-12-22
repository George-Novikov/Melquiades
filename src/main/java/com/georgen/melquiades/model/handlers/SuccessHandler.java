package com.georgen.melquiades.model.handlers;

import com.georgen.melquiades.model.trackers.Tracker;

@FunctionalInterface
public interface SuccessHandler {
    void handle(Tracker tracker);
}
