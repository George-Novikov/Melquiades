package com.georgen.melquiades.model;

public class ProfilerSettings {
    private Boolean isEnabled;
    private Integer threads = 1;
    private String homePath;
    private String fileName;
    private Integer samplingInterval = 1000;

    public Boolean getEnabled() { return isEnabled; }

    public void setEnabled(Boolean enabled) { isEnabled = enabled; }

    public Integer getThreads() { return threads; }

    public void setThreads(Integer threads) { this.threads = threads; }

    public String getHomePath() { return homePath; }

    public void setHomePath(String homePath) { this.homePath = homePath; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public Integer getSamplingInterval() { return samplingInterval; }

    public void setSamplingInterval(Integer samplingInterval) { this.samplingInterval = samplingInterval; }

    public static ProfilerSettings getDefault(){
        ProfilerSettings settings = new ProfilerSettings();
        settings.setEnabled(true);
        settings.setThreads(1);
        settings.setHomePath("/");
        settings.setFileName("profiler-log.jsonl");
        settings.setSamplingInterval(1000);
        return settings;
    }
}
