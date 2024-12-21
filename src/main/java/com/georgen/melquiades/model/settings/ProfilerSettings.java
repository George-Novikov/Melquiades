package com.georgen.melquiades.model.settings;

import java.io.File;

public class ProfilerSettings {

    public static int INTERVAL_THRESHOLD = 200;

    private Boolean isEnabled;
    private Integer threads;
    private String homePath;
    private String fileName;
    private Integer interval;
    private Metrics metrics;
    private Logging logging;
    private Blacklist blacklist;

    public Boolean isEnabled() { return isEnabled != null ? isEnabled : Boolean.FALSE; }

    public void setEnabled(Boolean enabled) { isEnabled = enabled; }

    public Integer getThreads() { return threads != null ? threads : 0; }

    public void setThreads(Integer threads) { this.threads = threads; }

    public String getHomePath() { return homePath; }

    public void setHomePath(String homePath) { this.homePath = homePath; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public Integer getInterval() { return interval != null ? interval : -1; }

    public void setInterval(Integer interval) { this.interval = interval; }

    public Metrics getMetrics() {
        if (metrics == null) metrics = new Metrics();
        return metrics;
    }

    public void setMetrics(Metrics metrics) { this.metrics = metrics; }

    public Logging getLogging() {
        if (logging == null) logging = new Logging();
        return logging;
    }

    public void setLogging(Logging logging) { this.logging = logging; }

    public Blacklist getBlacklist() {
        if (blacklist == null) blacklist = new Blacklist();
        return blacklist;
    }

    public void setBlacklist(Blacklist blacklist) { this.blacklist = blacklist; }

    public boolean isBlacklisted(String name, DataType type){
        return getBlacklist().isBlacklisted(name, type);
    }

    public void enable(){ this.isEnabled = Boolean.TRUE; }

    public void disable(){ this.isEnabled = Boolean.FALSE; }

    public void validate(){
        if (this.getHomePath() == null || this.getHomePath().isEmpty()) {
            throw new IllegalArgumentException("Profiler home path cannot be null or empty!");
        }

        if (this.getFileName() == null || this.getFileName().isEmpty()) {
            throw new IllegalArgumentException("Profiler file name cannot be null or empty!");
        }

        if (this.getInterval() == null || this.getInterval() < INTERVAL_THRESHOLD) {
            throw new IllegalArgumentException("Profiler update interval cannot be null of less than " + INTERVAL_THRESHOLD + " milliseconds");
        }

        this.getMetrics().validate();
    }

    public String getLogPath(){
        return this.getHomePath() + File.separator + this.getFileName();
    }

    public static ProfilerSettings getDefault(){
        ProfilerSettings settings = new ProfilerSettings();
        settings.setEnabled(Boolean.FALSE);
        settings.setThreads(2);
        settings.setHomePath("/");
        settings.setFileName("profiler.jsonl");
        settings.setInterval(1000);
        settings.setMetrics(Metrics.getDefault());
        settings.setLogging(Logging.getDefault());
        settings.setBlacklist(Blacklist.getDefault());
        return settings;
    }
}
