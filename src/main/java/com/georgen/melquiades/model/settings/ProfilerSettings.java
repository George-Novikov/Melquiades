package com.georgen.melquiades.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.handlers.ErrorHandler;
import com.georgen.melquiades.model.handlers.SuccessHandler;

import java.io.File;

@JsonPropertyOrder({"enabled", "threads", "interval", "homePath", "fileName", "emptyWrite", "metrics", "logging", "blacklist", "historyDepth"})
public class ProfilerSettings {

    public static int INTERVAL_THRESHOLD = 200; // Profiling anything in less than 200 milliseconds doesn't make sense

    private Boolean isEnabled;
    private Integer threads;
    private Integer interval;
    private String homePath;
    private String fileName;
    private Boolean isEmptyWrite;
    private Metrics metrics;
    private Logging logging;
    private Blacklist blacklist;
    private HistoryDepth historyDepth;

    public Boolean isEnabled() { return isEnabled != null ? isEnabled : Boolean.FALSE; }

    public void setEnabled(Boolean enabled) { isEnabled = enabled; }

    public Integer getThreads() { return threads != null ? threads : 0; }

    public void setThreads(Integer threads) { this.threads = threads; }

    public Integer getInterval() { return interval != null ? interval : -1; }

    public void setInterval(Integer interval) { this.interval = interval; }

    public String getHomePath() { return homePath; }

    public void setHomePath(String homePath) { this.homePath = homePath; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public Boolean isEmptyWrite() { return isEmptyWrite != null ? isEmptyWrite : Boolean.TRUE; }

    public void setEmptyWrite(Boolean emptyWrite) { isEmptyWrite = emptyWrite; }

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

    public HistoryDepth getHistoryDepth() {
        if (historyDepth == null) historyDepth = HistoryDepth.NONE;
        return historyDepth;
    }

    public void setHistoryDepth(HistoryDepth historyDepth) {
        this.historyDepth = historyDepth;
    }

    @JsonIgnore
    public boolean isHistoric(){
        return !this.getHistoryDepth().equals(HistoryDepth.NONE);
    }

    @JsonIgnore
    public boolean isBlacklisted(String name, DataType type){
        return getBlacklist().isBlacklisted(name, type);
    }

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

    @JsonIgnore
    public String getLogPath(){
        return this.getHomePath() + File.separator + this.getFileName();
    }

    // ============================================= Builder methods ============================================= //

    public ProfilerSettings enable(){
        this.isEnabled = Boolean.TRUE;
        return this;
    }

    public ProfilerSettings disable(){
        this.isEnabled = Boolean.FALSE;
        return this;
    }

    public ProfilerSettings threads(int threads){
        this.setThreads(threads);
        return this;
    }

    public ProfilerSettings interval(int interval){
        this.setInterval(interval);
        return this;
    }

    public ProfilerSettings homePath(String homePath){
        this.setHomePath(homePath);
        return this;
    }

    public ProfilerSettings fileName(String fileName){
        this.setFileName(fileName);
        return this;
    }

    public ProfilerSettings emptyWrite(Boolean isEmptyWrite){
        this.setEmptyWrite(isEmptyWrite);
        return this;
    }

    public ProfilerSettings metrics(Metrics metrics){
        this.setMetrics(metrics);
        return this;
    }

    public ProfilerSettings logging(Logging logging){
        this.setLogging(logging);
        return this;
    }

    public ProfilerSettings blacklist(Blacklist blacklist){
        this.setBlacklist(blacklist);
        return this;
    }

    public ProfilerSettings historyDepth(HistoryDepth depth){
        this.setHistoryDepth(depth);
        return this;
    }

    public ProfilerSettings errorHandler(ErrorHandler errorHandler){
        Profiler.getInstance().setErrorHandler(errorHandler);
        return this;
    }

    public ProfilerSettings successHandler(SuccessHandler successHandler){
        Profiler.getInstance().setSuccessHandler(successHandler);
        return this;
    }

    public ProfilerSettings launch(){
        Profiler.launch(this);
        return this;
    }

    // ============================================= Static methods ============================================= //

    public static ProfilerSettings getDefault(){
        ProfilerSettings settings = new ProfilerSettings();
        settings.setEnabled(Boolean.FALSE);
        settings.setThreads(2);
        settings.setHomePath(System.getProperty("user.dir"));
        settings.setFileName("profiler.jsonl");
        settings.setInterval(1000);
        settings.setMetrics(Metrics.getDefault());
        settings.setLogging(Logging.getDefault());
        settings.setBlacklist(Blacklist.getDefault());
        return settings;
    }
}
