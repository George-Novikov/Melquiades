package com.georgen.melquiades.core;

import com.georgen.melquiades.io.BufferAppender;
import com.georgen.melquiades.model.handlers.ErrorHandler;
import com.georgen.melquiades.model.ProfilerSettings;
import com.georgen.melquiades.model.trackers.Tracker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Profiler {

    private static class Holder {
        private static final Profiler INSTANCE = new Profiler();
    }

    public static Profiler getInstance() { return Holder.INSTANCE; }

    private ProfilerSettings settings;
    private ErrorHandler errorHandler;

    private ProfilerReport currentReport;
    private LocalDateTime lastUpdated;
    private ConcurrentMap<UUID, Tracker> runs;

    private Profiler(){ this(ProfilerSettings.getDefault()); }

    private Profiler(ProfilerSettings settings){
        this(settings, e -> {});
    }

    private Profiler(ProfilerSettings settings, ErrorHandler errorHandler){
        this.settings = settings;
        this.errorHandler = errorHandler;

        this.currentReport = new ProfilerReport();
        this.lastUpdated = LocalDateTime.now();
        this.runs = new ConcurrentHashMap<>();
    }

    public ProfilerReport getCurrentReport() { return currentReport; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }

    public ConcurrentMap<UUID, Tracker> getRuns() { return runs; }

    public ProfilerSettings getSettings() { return settings; }

    public void setSettings(ProfilerSettings settings) { this.settings = settings; }

    public ErrorHandler getErrorHandler() { return errorHandler; }

    public void setErrorHandler(ErrorHandler errorHandler) { this.errorHandler = errorHandler; }

    public void process(Tracker tracker){
        try {
            switch (tracker.getPhase()){
                case RUNNING:
                    processRunning(tracker);
                    return;
                case FINISHED:
                    processFinish(tracker);
                    return;
                case ERROR:
                    processError(tracker);
            }
        } catch (Exception e) {
            this.errorHandler.handle(e);
        }
    }

    private void processRunning(Tracker tracker) {
        getRuns().put(tracker.getUuid(), tracker);
    }

    private void processFinish(Tracker tracker) throws IOException {
        try (BufferAppender appender = new BufferAppender("profiler.log", this.errorHandler)) {
            appender.append(tracker.toString());
            getRuns().remove(tracker.getUuid());
        }
    }

    private void processError(Tracker tracker) {

    }
}
