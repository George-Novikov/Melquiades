package com.georgen.melquiades.core;

import com.alibaba.fastjson2.JSON;
import com.georgen.melquiades.io.BufferAppender;
import com.georgen.melquiades.model.data.DataRoot;
import com.georgen.melquiades.model.handlers.ErrorHandler;
import com.georgen.melquiades.model.settings.Logging;
import com.georgen.melquiades.model.settings.Metrics;
import com.georgen.melquiades.model.settings.ProfilerSettings;
import com.georgen.melquiades.model.trackers.Tracker;

import java.util.concurrent.*;

public class Profiler {

    private ProfilerSettings settings;
    private ScheduledExecutorService scheduler;
    private ExecutorService executor;
    private ErrorHandler errorHandler;
    private DataRoot data;

    private Profiler(){ this(ProfilerSettings.getDefault()); }

    //TODO: default ErrorHandler (profiler-error.log)
    private Profiler(ProfilerSettings settings){
        this(settings, e -> {});
    }

    private Profiler(ProfilerSettings settings, ErrorHandler errorHandler){
        validate(settings);
        this.settings = settings;
        this.errorHandler = errorHandler;
        this.data = new DataRoot();
        this.init(settings);
    }

    public DataRoot getData() { return data; }

    public ProfilerSettings getSettings() {
        if (settings == null) settings = ProfilerSettings.getDefault();
        return settings;
    }

    public void setSettings(ProfilerSettings settings) {
        validate(settings);
        this.settings = settings;
    }

    public ErrorHandler getErrorHandler() { return errorHandler; }

    public void setErrorHandler(ErrorHandler errorHandler) { this.errorHandler = errorHandler; }

    public boolean isWorking(){ return scheduler != null; }

    public Profiler init(ProfilerSettings settings){
        this.setSettings(settings);

        int threads = settings.getThreads();

        if (threads <= 0 || !settings.isEnabled()) {
            settings.setEnabled(Boolean.FALSE);
            return this;
        }

        int schedulerThreads = threads % 2 == 0 ? threads / 2 : threads / 2 + 1;
        int executorThreads = threads / 2;

        // At this point schedulerThreads is always a positive number
        scheduler = Executors.newScheduledThreadPool(schedulerThreads);
        scheduler.schedule(this::rotateReport, settings.getInterval(), TimeUnit.SECONDS);

        if (executorThreads > 0){
            executor = Executors.newFixedThreadPool(executorThreads);
        } else {
            executor = null; // This is needed to switch multithreaded mode to single threaded when changing settings
        }

        return this;
    }

    private void rotateReport(){
        try {
            String jsonReport = JSON.toJSONString(this.data);
            try (BufferAppender appender = new BufferAppender(settings.getLogPath())) {
                appender.append(jsonReport);
            }
            this.data = new DataRoot();
        } catch (Exception e){
            this.errorHandler.handle(e);
        }
    }

    public void process(Tracker tracker){
        if (tracker == null || !tracker.isValid()) return;
        if (!isEnabled()) return;
        healOrBypass();

        if (isSingleThreaded()){
            tryProcess(tracker);
        } else {
            this.executor.submit(() -> tryProcess(tracker));
        }
    }

    private boolean isSingleThreaded(){
        return this.executor == null || this.executor.isShutdown() || this.executor.isTerminated();
    }

    private void tryProcess(Tracker tracker){
        try {
            if (this.data == null) this.data = new DataRoot();
            this.data.register(tracker);
        } catch (Exception e) {
            this.errorHandler.handle(e);
        }
    }

    private void healOrBypass(){
        if (!settings().isEnabled() || this.scheduler == null) return;

        if (this.scheduler.isShutdown() || this.scheduler.isTerminated()){
            this.init(this.settings);
        }
    }

    private void validate(ProfilerSettings settings){
        if (settings == null) throw new IllegalArgumentException("Profiler settings cannot be null");
        settings.validate();
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

    public static Logging logging(){
        return settings().getLogging();
    }

    public static boolean isEnabled(){
        if (!getInstance().isWorking()) settings().setEnabled(Boolean.FALSE);
        return settings().isEnabled();
    }
}
