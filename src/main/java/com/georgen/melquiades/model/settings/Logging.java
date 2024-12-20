package com.georgen.melquiades.model.settings;

import java.util.Arrays;
import java.util.List;

public class Logging {
    private List<LoggingPolicy> clusters;
    private List<LoggingPolicy> groups;
    private List<LoggingPolicy> processes;

    public List<LoggingPolicy> getClusters() { return clusters; }

    public void setClusters(List<LoggingPolicy> clusters) { this.clusters = clusters; }

    public List<LoggingPolicy> getGroups() { return groups; }

    public void setGroups(List<LoggingPolicy> groups) { this.groups = groups; }

    public List<LoggingPolicy> getProcesses() { return processes; }

    public void setProcesses(List<LoggingPolicy> processes) { this.processes = processes; }

    public static Logging getDefault(){
        Logging logging = new Logging();
        logging.setClusters(Arrays.asList(LoggingPolicy.FULL));
        logging.setGroups(Arrays.asList(LoggingPolicy.FULL));
        logging.setProcesses(Arrays.asList(LoggingPolicy.FULL));
        return logging;
    }
}
