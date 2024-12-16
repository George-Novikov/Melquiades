package com.georgen.melquiades.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Statistics {

    public static <T> int rank(Collection<T> input, double percentile){
        return percentile == 0 ? 1 : (int) Math.ceil(percentile / 100.0 * input.size());
    }

    public static <T extends Comparable<Double>> Double percentile(Collection<Double> input, double percentile) {
        validate(input, percentile);
        List<Double> sorted = input.stream().sorted().collect(Collectors.toList());
        int rank = rank(input, percentile);
        return sorted.get(rank - 1);
    }

    public static <T extends Comparable<Double>> Double avg(Collection<Double> input){
        return input.stream().mapToDouble(x -> x).average().orElse(0.0);
    }

    private static <T> void validate(Collection<T> input, double percentile){
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("The input dataset cannot be null or empty.");
        }
        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Percentile must be between 0 and 100 inclusive.");
        }
    }
}
