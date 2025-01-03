package com.georgen.melquiades.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.georgen.melquiades.core.trackers.Tracker;
import com.georgen.melquiades.util.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Hits {
    private int total;
    private int running;
    private int success;
    private int errors;
    @JsonIgnore
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

    public void register(Tracker tracker){
        if (!tracker.hasPhase()) return;

        switch (tracker.getPhase()){
            case RUNNING:{
                plusRun(tracker.getUuid());
                return;
            }
            case FINISHED: {
                plusSuccess(tracker.getUuid());
                return;
            }
            case ERROR: {
                plusError(tracker.getUuid());
            }
        }
    }

    public void calculate(){
        this.total = this.running + this.success + this.errors;
    }

    public void averageWith(Hits outerHits){
        if (outerHits == null) return;
        this.total = (this.total + outerHits.getTotal()) / 2;
        this.running = (this.running + outerHits.getRunning()) / 2;
        this.success = (this.success + outerHits.getSuccess()) / 2;
        this.errors = (this.errors + outerHits.getErrors()) / 2;
    }

    @Override
    public String toString() {
        try {
            return Serializer.serialize(this);
        } catch (Exception e){
            return "{}";
        }
    }

    public static Hits ofBatch(List<Hits> hitsList){
        Hits hits = new Hits();
        hits.setTotal(hitsList.stream().mapToInt(Hits::getTotal).sum());
        hits.setRunning(hitsList.stream().mapToInt(Hits::getRunning).sum());
        hits.setSuccess(hitsList.stream().mapToInt(Hits::getSuccess).sum());
        hits.setErrors(hitsList.stream().mapToInt(Hits::getErrors).sum());
        return hits;
    }
}
