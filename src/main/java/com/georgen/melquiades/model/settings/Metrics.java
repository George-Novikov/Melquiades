package com.georgen.melquiades.model.settings;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Metrics extends ArrayList<String> {

    public static final String AVG = "avg";
    public static final String MODE = "mode";
    public static final String MAX = "max";
    public static final String MIN = "min";

    public static final String P50 = "p50";
    public static final String P75 = "p75";
    public static final String P95 = "p95";

    private static final String PERCENTILE_REGEX = "^p[1-9][0-9]{0,2}$";

    public void validate(){
        this.forEach(value -> {
            if (!isValid(value)){
                throw new IllegalArgumentException(
                        String.format("Value %s is not a valid metric", value)
                );
            }
        });
    }

    public static boolean isValid(String value){
        if (value == null || value.isEmpty()) return false;

        if (value.equals(AVG) || value.equals(MODE) || value.equals(MAX) || value.equals(MIN)) {
            return true;
        }

        return isPercentile(value);
    }

    public static boolean isAvg(String metric){ return AVG.equals(metric); }

    public static boolean isMode(String metric){ return MODE.equals(metric); }

    public static boolean isMax(String metric){ return MAX.equals(metric); }

    public static boolean isMin(String metric){ return MIN.equals(metric); }

    public static boolean isPercentile(String value){
        return Pattern.matches(PERCENTILE_REGEX, value);
    }

    public static Metrics getDefault(){
        Metrics metrics = new Metrics();
        metrics.add(AVG);
        metrics.add(P50);
        metrics.add(P75);
        metrics.add(P95);
        return metrics;
    }
}
