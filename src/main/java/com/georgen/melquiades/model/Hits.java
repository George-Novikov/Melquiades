package com.georgen.melquiades.model;

import com.alibaba.fastjson2.annotation.JSONField;
import com.georgen.melquiades.model.trackers.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Hits {
    private int total;
    private int running;
    private int success;
    private int errors;
    @JSONField(serialize = false)
    private List<UUID> runs = new ArrayList<>();

    public int getTotal() { return total; }

    public void setTotal(int total) { this.total = total; }

    public int getRunning() { return running; }

    public void setRunning(int running) { this.running = running; }

    public int getSuccess() { return success; }

    public void setSuccess(int success) { this.success = success; }

    public int getErrors() { return errors; }

    public void setErrors(int errors) { this.errors = errors; }

    public List<UUID> getRuns() { return runs; }

    public void setRuns(List<UUID> runs) { this.runs = runs; }

    public boolean hasRun(UUID uuid){ return this.runs.contains(uuid); }

    public void plusRun(Tracker tracker){
        this.plusRun(tracker.getUuid());
    }

    public void plusSuccess(Tracker tracker){
        this.plusSuccess(tracker.getUuid());
    }

    public void plusError(Tracker tracker){
        this.plusError(tracker.getUuid());
    }

    public void plusRun(UUID uuid) {
        this.runs.add(uuid);
        this.running++;
    }

    public void minusRun(UUID uuid) {
        this.runs.remove(uuid);
        this.running--;
    }

    public void plusSuccess(UUID uuid){
        if (hasRun(uuid)) minusRun(uuid);
        this.success++;
    }

    public void plusError(UUID uuid){
        if (hasRun(uuid)) minusRun(uuid);
        this.errors++;
    }
}
