package com.georgen.melquiades.core;

import com.georgen.melquiades.model.ProfilerReport;
import com.georgen.melquiades.model.handlers.ErrorHandler;
import com.georgen.melquiades.model.settings.Metrics;
import com.georgen.melquiades.model.settings.ProfilerSettings;
import com.georgen.melquiades.model.trackers.Tracker;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.*;

public class Profiler {

    private ProfilerSettings settings;
    private ScheduledExecutorService scheduler;
    private ExecutorService executor;
    private ErrorHandler errorHandler;

    private ProfilerReport report;
    private LocalDateTime lastUpdated;
    private ConcurrentMap<UUID, Tracker> runs;

    private Profiler(){ this(ProfilerSettings.getDefault()); }

    private Profiler(ProfilerSettings settings){
        this(settings, e -> {});
    }

    private Profiler(ProfilerSettings settings, ErrorHandler errorHandler){
        this.settings = settings;
        this.scheduler = Executors.newScheduledThreadPool(settings.getThreads());
        this.errorHandler = errorHandler;

        this.report = new ProfilerReport();
        this.lastUpdated = LocalDateTime.now();
        this.runs = new ConcurrentHashMap<>();
    }

    public ProfilerReport getReport() { return report; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }

    public ConcurrentMap<UUID, Tracker> getRuns() { return runs; }

    public ProfilerSettings getSettings() { return settings != null ? settings : ProfilerSettings.getDefault(); }

    public void setSettings(ProfilerSettings settings) { this.settings = settings; }

    public ErrorHandler getErrorHandler() { return errorHandler; }

    public void setErrorHandler(ErrorHandler errorHandler) { this.errorHandler = errorHandler; }

    public boolean hasSettings(){ return settings != null; }

    public Profiler init(ProfilerSettings settings){
        int threads = settings.getThreads();

        if (threads <= 0 || !settings.isEnabled()) {
            settings.setEnabled(Boolean.FALSE);
            return this;
        }

        int schedulerThreads = threads % 2 == 0 ? threads / 2 : threads / 2 + 1;
        int executorThreads = threads / 2;

        // At this point schedulerThreads is always a positive number
        scheduler = Executors.newScheduledThreadPool(schedulerThreads);
        if (executorThreads > 0){
            executor = Executors.newFixedThreadPool(executorThreads);
        }

        return this;
    }

    public void process(Tracker tracker){
        if (tracker == null || !tracker.isValid()) return;

        if (isSync()){
            tryProcess(tracker);
        } else {
            this.executor.submit(() -> tryProcess(tracker));
        }
    }

    private boolean isSync(){
        return this.executor == null || this.executor.isShutdown() || this.executor.isTerminated();
    }

    private void tryProcess(Tracker tracker){
        try {
            if (this.report == null) this.report = new ProfilerReport();
            this.report.register(tracker);
        } catch (Exception e) {
            this.errorHandler.handle(e);
        }
    }

    /** Thread-safe wrapper (Bill Pugh Singleton). Do not refactor. */
    private static class Holder {
        private static final Profiler INSTANCE = new Profiler();
    }

    public static Profiler getInstance() { return Holder.INSTANCE; }

    public static Profiler launch(ProfilerSettings settings){
        return getInstance().init(settings);
    }

    public static ProfilerSettings settings(){
        return getInstance().getSettings();
    }

    public static Metrics metrics(){
        return settings().getMetrics();
    }

    public static boolean isEnabled(){
        return settings().isEnabled();
    }
}
