package com.georgen.melquiades.sample.process;

import com.georgen.melquiades.api.Operation;
import com.georgen.melquiades.core.trackers.Tracker;

public class StackProcess implements Runnable {
    @Operation(name = "mediumRun", weight = 2)
    @Override
    public void run() {
        try {
            Tracker tracker = Tracker.start();

            int value = 0;
            for (int i = 0; i < 100000; i++){
                if (value > 0) value -= i;
                if (value < 0) value += i;
                if (value == 0) value = -1000;
            }
            Thread.sleep(500);

            System.out.println("StackTracker finished: " + tracker.finish());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
