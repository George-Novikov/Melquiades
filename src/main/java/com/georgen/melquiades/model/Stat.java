package com.georgen.melquiades.model;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.settings.Metrics;
import com.georgen.melquiades.util.Statistics;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Stat extends ConcurrentHashMap<String, Double> {

    public static Stat of(List<Double> dataset){
        Metrics metrics = Profiler.metrics();

        Stat stat = new Stat();
        metrics.forEach(metric -> {
            if (Metrics.isAvg(metric)) stat.put(Metrics.AVG, Statistics.avg(dataset));
            if (Metrics.isMode(metric)) stat.put(Metrics.MODE, Statistics.mode(dataset));
            if (Metrics.isMax(metric)) stat.put(Metrics.MAX, Statistics.max(dataset));
            if (Metrics.isMin(metric)) stat.put(Metrics.MIN, Statistics.min(dataset));

            if (Metrics.isPercentile(metric)){
                int percentile = Integer.parseInt(metric.substring(1));
                stat.put(metric, Statistics.percentile(dataset, percentile));
            }
        });

        return stat;
    }

    public static Stat ofBatch(List<Stat> batch){
        Metrics metrics = Profiler.metrics();

        Stat stat = new Stat();

        metrics.forEach(metric -> {
            if (Metrics.isAvg(metric)){
                List<Double> avgList = batch.stream().map(s -> s.get(Metrics.AVG)).collect(Collectors.toList());
                stat.put(Metrics.AVG, Statistics.avg(avgList));
            }

            if (Metrics.isMode(metric)){
                List<Double> modeList = batch.stream().map(s -> s.get(Metrics.MODE)).collect(Collectors.toList());
                stat.put(Metrics.MODE, Statistics.mode(modeList));
            }

            if (Metrics.isMax(metric)){
                List<Double> maxList = batch.stream().map(s -> s.get(Metrics.MAX)).collect(Collectors.toList());
                stat.put(Metrics.MAX, Statistics.max(maxList));
            }

            if (Metrics.isMin(metric)){
                List<Double> minList = batch.stream().map(s -> s.get(Metrics.MIN)).collect(Collectors.toList());
                stat.put(Metrics.MIN, Statistics.min(minList));
            }

            if (Metrics.isPercentile(metric)){
                int percentile = Integer.parseInt(metric.substring(1));
                List<Double> percList = batch.stream().map(s -> s.get(metric)).collect(Collectors.toList());
                stat.put(Metrics.MIN, Statistics.percentile(percList, percentile));
            }
        });

        return stat;
    }

}
