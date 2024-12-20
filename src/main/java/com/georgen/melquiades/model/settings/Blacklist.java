package com.georgen.melquiades.model.settings;

import java.util.List;

public class Blacklist {
    private List<String> clusters;
    private List<String> groups;
    private List<String> processes;

    public List<String> getClusters() { return clusters; }

    public void setClusters(List<String> clusters) { this.clusters = clusters; }

    public List<String> getGroups() { return groups; }

    public void setGroups(List<String> groups) { this.groups = groups; }

    public List<String> getProcesses() { return processes; }

    public void setProcesses(List<String> processes) { this.processes = processes; }

    public boolean isBlacklisted(String name, DataType type){
        if (name == null || name.isEmpty() || type == null) return true; // all rubbish is sifted out

        switch (type){
            case CLUSTER: return clusters != null && clusters.contains(name);
            case GROUP: return groups != null && groups.contains(name);
            default: return processes != null && processes.contains(name);
        }
    }

    public static Blacklist getDefault(){
        return new Blacklist();
    }
}
