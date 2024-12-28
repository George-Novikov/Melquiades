package com.georgen.melquiades.core;

import com.georgen.melquiades.io.BufferAppender;
import com.georgen.melquiades.model.data.DataRoot;
import com.georgen.melquiades.model.handlers.ErrorHandler;
import com.georgen.melquiades.model.handlers.ErrorLogger;
import com.georgen.melquiades.model.handlers.SuccessHandler;
import com.georgen.melquiades.model.settings.Logging;
import com.georgen.melquiades.model.settings.Metrics;
import com.georgen.melquiades.model.settings.ProfilerSettings;
import com.georgen.melquiades.model.trackers.Tracker;
import com.georgen.melquiades.util.LogRotator;
import com.georgen.melquiades.util.Serializer;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

public class Profiler implements Closeable {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss.SSS");

    private ProfilerSettings settings;
    private ScheduledExecutorService scheduler;
    private ExecutorService executor;
    private ErrorHandler errorHandler;
    private SuccessHandler successHandler;
    private DataRoot data;
    private boolean isWorking;
    private LocalDate today;


    // ============================================= Constructors ============================================= //

    private Profiler(){
        this(ProfilerSettings.getDefault());
    }

    private Profiler(ProfilerSettings settings){
        this(settings, new ErrorLogger());
    }

    private Profiler(ProfilerSettings settings, ErrorHandler errorHandler){
        validate(settings);
        this.settings = settings;
        this.errorHandler = errorHandler;
        this.data = new DataRoot();
        this.init(settings);
    }


    // ============================================= Accessors ============================================= //

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

    public SuccessHandler getSuccessHandler() { return successHandler; }

    public void setSuccessHandler(SuccessHandler successHandler) { this.successHandler = successHandler; }


    // ============================================= Public ============================================= //

    public Profiler init(ProfilerSettings settings){
        this.setSettings(settings);

        int threads = settings.getThreads();

        if (threads <= 0 || !settings.isEnabled()) {
            settings.setEnabled(Boolean.FALSE);
            return this;
        }

        int schedulerThreads = threads % 2 == 0 ? threads / 2 : threads / 2 + 1;
        int executorThreads = threads / 2;
        int interval = settings.getInterval();

        // At this point schedulerThreads is always a positive number
        scheduler = Executors.newScheduledThreadPool(schedulerThreads);
        scheduler.scheduleAtFixedRate(this::rotateReport, interval, interval, TimeUnit.MILLISECONDS);

        if (executorThreads > 0){
            executor = Executors.newFixedThreadPool(executorThreads);
        } else {
            executor = null; // This is necessary to switch multithreaded mode to single threaded when changing settings
        }

        this.isWorking = true;
        return this;
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

    @Override
    public void close(){
        shutdownExecutor(scheduler);
        shutdownExecutor(executor);
        this.isWorking = false;
    }

    public boolean isWorking(){ return scheduler != null && isWorking; }

    public boolean isMultiThreaded(){ return executor != null; }


    // ============================================= Private ============================================= //

    private void shutdownExecutor(ExecutorService executor){
        if (executor != null && !executor.isShutdown()){
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    private void rotateReport(){
        try {
            performDayCheck();
            this.data.calculate();
            String jsonReport = Serializer.serialize(this.data);
            try (BufferAppender appender = new BufferAppender(settings.getLogPath())) {
                appender.append(jsonReport);
            }
            this.data = new DataRoot();
        } catch (Exception e){
            this.errorHandler.handle(e);
        }
    }

    private boolean isSingleThreaded(){
        return this.executor == null || this.executor.isShutdown() || this.executor.isTerminated();
    }

    private void tryProcess(Tracker tracker){
        try {
            if (this.data == null) this.data = new DataRoot();
            this.data.register(tracker);
            if (successHandler != null) successHandler.handle(tracker);
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

    private void performDayCheck() throws IOException {
        if (today == null || today.isBefore(LocalDate.now())){
            if (LogRotator.isOldFile(settings.getLogPath())){
                LogRotator.zipRotate(settings.getLogPath());
            }

            today = LocalDate.now();
        }
    }


    // ============================================= Static ============================================= //

    /** Thread-safe wrapper (Bill Pugh Singleton). Do not refactor. */
    private static class Holder {
        private static final Profiler INSTANCE = new Profiler();
    }

    public static Profiler getInstance() { return Holder.INSTANCE; }

    public static Profiler launch(){
        return launch(settings());
    }

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

    public static void errorHandler(ErrorHandler errorHandler){
        getInstance().setErrorHandler(errorHandler);
    }

    public static void successHandler(SuccessHandler successHandler){
        getInstance().setSuccessHandler(successHandler);
    }

    public static boolean isEnabled(){
        if (!getInstance().isWorking()) settings().setEnabled(Boolean.FALSE);
        return settings().isEnabled();
    }

    public static List<DataRoot> report(LocalDateTime start, LocalDateTime finish) throws IOException {
        return ReportBuilder.getReport(start, finish);
    }

    public static void shutdown(){
        getInstance().close();
    }
}
