package com.georgen.melquiades.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.georgen.melquiades.core.trackers.Tracker;
import com.georgen.melquiades.util.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Hits {
    private final AtomicInteger total = new AtomicInteger(0);
    private final AtomicInteger running = new AtomicInteger(0);
    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger errors = new AtomicInteger(0);
    @JsonIgnore
    private List<UUID> runs = new ArrayList<>();

    public int getTotal() { return total.get(); }

    public void setTotal(int total) { this.total.set(total); }

    public int getRunning() { return running.get(); }

    public void setRunning(int running) { this.running.set(running); }

    public int getSuccess() { return success.get(); }

    public void setSuccess(int success) { this.success.set(success); }

    public int getErrors() { return errors.get(); }

    public void setErrors(int errors) { this.errors.set(errors); }

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
        this.running.incrementAndGet();
    }

    public void minusRun(UUID uuid) {
        this.runs.remove(uuid);
        this.running.decrementAndGet();
    }

    public void plusSuccess(UUID uuid){
        if (hasRun(uuid)) minusRun(uuid);
        this.success.incrementAndGet();
    }

    public void plusError(UUID uuid){
        if (hasRun(uuid)) minusRun(uuid);
        this.errors.incrementAndGet();
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
        this.total.set(this.running.get() + this.success.get() + this.errors.get());
    }

    public void averageWith(Hits outerHits){
        if (outerHits == null) return;
        this.total.set((this.total.get() + outerHits.getTotal()) / 2);
        this.running.set((this.running.get() + outerHits.getRunning()) / 2);
        this.success.set((this.success.get() + outerHits.getSuccess()) / 2);
        this.errors.set((this.errors.get() + outerHits.getErrors()) / 2);
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
