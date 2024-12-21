package com.georgen.melquiades.model.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.georgen.melquiades.model.settings.LoggingPolicy.*;

public class Logging {
    private List<LoggingPolicy> root;
    private List<LoggingPolicy> clusters;
    private List<LoggingPolicy> groups;
    private List<LoggingPolicy> processes;

    public List<LoggingPolicy> getRoot() {
        if (root == null) root = new ArrayList<>();
        return root;
    }

    public void setRoot(List<LoggingPolicy> root) {
        this.root = root;
    }

    public List<LoggingPolicy> getClusters() {
        if (this.clusters == null) clusters = new ArrayList<LoggingPolicy>();
        return clusters;
    }

    public void setClusters(List<LoggingPolicy> clusters) { this.clusters = clusters; }

    public List<LoggingPolicy> getGroups() {
        if (this.groups == null) groups = new ArrayList<LoggingPolicy>();
        return groups;
    }

    public void setGroups(List<LoggingPolicy> groups) { this.groups = groups; }

    public List<LoggingPolicy> getProcesses() {
        if (this.processes == null) processes = new ArrayList<LoggingPolicy>();
        return processes;
    }

    public void setProcesses(List<LoggingPolicy> processes) { this.processes = processes; }

    public boolean isRootPolicy(LoggingPolicy policy){
        return getRoot().contains(policy);
    }

    public boolean isClusterPolicy(LoggingPolicy policy){
        return getClusters().contains(policy);
    }

    public boolean isGroupPolicy(LoggingPolicy policy){
        return getGroups().contains(policy);
    }

    public boolean isProcessPolicy(LoggingPolicy policy){
        return getProcesses().contains(policy);
    }

    public static Logging getDefault(){
        Logging logging = new Logging();
        logging.setRoot(Arrays.asList(HITS, STAT, DATA));
        logging.setClusters(Arrays.asList(HITS, STAT, DATA));
        logging.setGroups(Arrays.asList(HITS, STAT, DATA));
        logging.setProcesses(Arrays.asList(HITS, STAT, DATA));
        return logging;
    }
}
