package com.georgen.melquiades.util;

import com.georgen.melquiades.model.settings.Metrics;

import java.util.*;
import java.util.stream.Collectors;

public class Statistics {

    public static <T> int rank(Collection<T> dataset, double percentile){
        return percentile == 0 ? 1 : (int) Math.ceil(percentile / 100.0 * dataset.size());
    }

    public static <T extends Comparable<Double>> double percentile(double percentile, Collection<Double> dataset) {
        validate(dataset, percentile);
        if (dataset == null || dataset.isEmpty()) return -1;
        List<Double> sorted = dataset.stream().sorted().collect(Collectors.toList());
        int rank = rank(dataset, percentile);
        return sorted.get(rank - 1);
    }

    public static <T extends Comparable<Double>> double avg(Collection<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) return -1;
        return Math.floor(dataset.stream().mapToDouble(x -> x).average().orElse(0.0) * 1000) / 1000; // rounding to 3 signs after dot
    }

    public static <T extends Comparable<Double>> double mode(Collection<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) return -1;

        Map<Double, Integer> frequencyMap = new HashMap<>();

        for (Double num : dataset) {
            frequencyMap.merge(num, 1, Integer::sum);
        }

        int maxFrequency = Collections.max(frequencyMap.values());
        if (maxFrequency == 1) return 0;

        List<Double> modes = frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return modes.get(0);
    }

    public static <T extends Comparable<Double>> double max(Collection<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) return -1;
        return dataset.stream().mapToDouble(x -> x).max().orElse(0.0);
    }

    public static <T extends Comparable<Double>> double min(Collection<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) return -1;
        return dataset.stream().mapToDouble(x -> x).min().orElse(0.0);
    }

    public static <T extends Comparable<Double>> double percentile(double percentile, Double... dataset){
        return percentile(percentile, Arrays.asList(dataset));
    }

    public static <T extends Comparable<Double>> double avg(Double... dataset){
        return avg(Arrays.asList(dataset));
    }

    public static <T extends Comparable<Double>> double mode(Double... dataset){
        return mode(Arrays.asList(dataset));
    }

    public static <T extends Comparable<Double>> double max(Double... dataset){
        return max(Arrays.asList(dataset));
    }

    public static <T extends Comparable<Double>> double min(Double... dataset){
        return min(Arrays.asList(dataset));
    }

    public static <T extends Comparable<Double>> double calculate(Collection<Double> dataset, String metric){
        if (dataset == null || dataset.isEmpty() || metric == null || metric.isEmpty()) return -1;

        if (Metrics.isPercentile(metric)){
            int percentile = Integer.parseInt(metric.substring(1));
            return percentile(percentile, dataset);
        }

        switch (metric){
            case Metrics.AVG: return avg(dataset);
            case Metrics.MODE: return mode(dataset);
            case Metrics.MAX: return max(dataset);
            case Metrics.MIN: return min(dataset);
            default: return -1;
        }
    }

    private static <T> void validate(Collection<T> dataset, double percentile) {
        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Percentile must be between 0 and 100 inclusive.");
        }
    }
}
