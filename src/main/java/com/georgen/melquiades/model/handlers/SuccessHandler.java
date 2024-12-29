package com.georgen.melquiades.model.handlers;

import com.georgen.melquiades.core.trackers.Tracker;

@FunctionalInterface
public interface SuccessHandler {
    void handle(Tracker tracker);
}
