package com.georgen.melquiades.model;

import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.model.settings.Metrics;
import com.georgen.melquiades.util.Serializer;
import com.georgen.melquiades.util.Statistics;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Stat extends ConcurrentHashMap<String, Double> {

    public void averageWith(Stat outerStat){
        if (outerStat == null) return;
        outerStat.forEach((k,v) -> {
            Double innerValue = this.get(k);
            if (innerValue == null || v == null) return;

            if (Metrics.isPercentile(k)){
                int percentile = Integer.parseInt(k.substring(1));
                double pcValue = Statistics.percentile(percentile, innerValue, v);
                this.put(k, pcValue);
            } else {
                this.put(k, (innerValue + v) / 2);
            }
        });
    }

    @Override
    public String toString() {
        try {
            return Serializer.serialize(this);
        } catch (Exception e){
            return "{}";
        }
    }

    public static Stat of(Collection<Double> dataset){
        Stat stat = new Stat();
        if (dataset == null || dataset.isEmpty()) return stat;

        Profiler.metrics().forEach(metric -> {
            double metricValue = Statistics.calculate(dataset, metric);
            if (metricValue >= 0) stat.put(metric, metricValue);
        });

        return stat;
    }

    public static Stat ofBatch(List<Stat> batch){
        Stat stat = new Stat();
        if (batch == null || batch.isEmpty()) return stat;

        Profiler.metrics().forEach(metric -> {
            List<Double> doubles = toDoubles(batch, metric);
            double metricValue = Statistics.calculate(doubles, metric);
            if (metricValue >= 0) stat.put(metric, metricValue);
        });

        return stat;
    }

    private static List<Double> toDoubles(List<Stat> batch, String metric){
        return batch.stream()
                .filter(Objects::nonNull)
                .map(s -> s.get(metric))
                .filter(v -> v != null && v >= 0)
                .collect(Collectors.toList());
    }
}
