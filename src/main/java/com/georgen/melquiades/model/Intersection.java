package com.georgen.melquiades.model;

import com.georgen.melquiades.model.data.*;
import com.georgen.melquiades.model.settings.DataType;
import com.georgen.melquiades.model.settings.Metrics;
import com.georgen.melquiades.util.Serializer;
import com.georgen.melquiades.util.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Intersection {

    private DataType type;
    private String first;
    private String second;
    private int hits;
    private String metric;
    private double value;

    public DataType getType() { return type; }

    public void setType(DataType type) { this.type = type; }

    public String getFirst() { return first; }

    public void setFirst(String first) { this.first = first; }

    public String getSecond() { return second; }

    public void setSecond(String second) { this.second = second; }

    public int getHits() { return hits; }

    public void setHits(int hits) { this.hits = hits; }

    public String getMetric() { return metric; }

    public void setMetric(String metric) { this.metric = metric; }

    public double getValue() { return value; }

    public void setValue(double value) { this.value = value; }

    @Override
    public String toString(){
        try {
            return Serializer.serialize(this);
        } catch (Exception e){
            return "{}";
        }
    }

    public static List<Intersection> of(DataRoot root, String metric){
        List<Intersection> intersections = new ArrayList<>();
        Map<String, DataCluster> clusters = root.getData();
        clusters.forEach((k1,v1)-> {
            clusters.forEach((k2,v2) -> {
                Intersection intersection = Intersection.of(k1, v1, k2, v2, metric, DataType.CLUSTER);
                intersections.add(intersection);
            });
        });
        return intersections;
    }

    public static List<Intersection> of(DataCluster root, String metric){
        List<Intersection> intersections = new ArrayList<>();
        Map<String, DataGroup> groups = root.getData();
        groups.forEach((k1,v1)-> {
            groups.forEach((k2,v2) -> {
                Intersection intersection = Intersection.of(k1, v1, k2, v2, metric, DataType.GROUP);
                intersections.add(intersection);
            });
        });
        return intersections;
    }

    public static List<Intersection> of(DataGroup root, String metric){
        List<Intersection> intersections = new ArrayList<>();
        Map<String, DataProcess> processes = root.getData();
        processes.forEach((k1,v1)-> {
            processes.forEach((k2,v2) -> {
                Intersection intersection = Intersection.of(k1, v1, k2, v2, metric, DataType.GROUP);
                intersections.add(intersection);
            });
        });
        return intersections;
    }

    public static Intersection of(String k1, Data v1, String k2, Data v2, String metric, DataType type){
        Intersection intersection = new Intersection();
        intersection.setType(type);
        intersection.setFirst(k1);
        intersection.setSecond(k2);

        if (v1.hasHits() && v2.hasHits()){
            intersection.setHits(v1.getHits().getTotal() + v2.getHits().getTotal());
        }

        if (v1.hasStat() && v2.hasStat()) {
            intersection.setMetric(metric);
            double metricValue1 = v1.getStat().get(metric);
            double metricValue2 = v2.getStat().get(metric);

            if (Metrics.isAvg(metric)) {
                intersection.setValue(Statistics.avg(metricValue1, metricValue2));
            }

            if (Metrics.isMode(metric)) {
                intersection.setValue(Statistics.mode(metricValue1, metricValue2));
            }

            if (Metrics.isMax(metric)) {
                intersection.setValue(Statistics.max(metricValue1, metricValue2));
            }

            if (Metrics.isMin(metric)) {
                intersection.setValue(Statistics.min(metricValue1, metricValue2));
            }

            if (Metrics.isPercentile(metric)) {
                int percentile = Integer.parseInt(metric.substring(1));
                intersection.setValue(Statistics.percentile(percentile, metricValue1, metricValue2));
            }
        }
        return intersection;
    }
}
