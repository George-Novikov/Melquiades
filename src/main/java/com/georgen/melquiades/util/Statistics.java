package com.georgen.melquiades.util;

import java.util.*;
import java.util.stream.Collectors;

public class Statistics {

    public static <T> int rank(Collection<T> dataset, double percentile){
        return percentile == 0 ? 1 : (int) Math.ceil(percentile / 100.0 * dataset.size());
    }

    public static <T extends Comparable<Double>> double percentile(Collection<Double> dataset, double percentile) {
        validate(dataset, percentile);
        List<Double> sorted = dataset.stream().sorted().collect(Collectors.toList());
        int rank = rank(dataset, percentile);
        return sorted.get(rank - 1);
    }

    public static <T extends Comparable<Double>> double avg(Collection<Double> dataset){
        return dataset.stream().mapToDouble(x -> x).average().orElse(0.0);
    }

    public static <T extends Comparable<Double>> double mode(Collection<Double> dataset) {
        if (dataset == null || dataset.isEmpty()) return 0;

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

    public static <T extends Comparable<Double>> double max(Collection<Double> dataset){
        return dataset.stream().mapToDouble(x -> x).max().orElse(0.0);
    }

    public static <T extends Comparable<Double>> double min(Collection<Double> dataset){
        return dataset.stream().mapToDouble(x -> x).min().orElse(0.0);
    }

    private static <T> void validate(Collection<T> dataset, double percentile){
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("The input dataset cannot be null or empty.");
        }
        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Percentile must be between 0 and 100 inclusive.");
        }
    }
}
