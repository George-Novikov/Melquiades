package com.georgen.melquiades.sample.process;

import com.georgen.melquiades.api.Operation;
import com.georgen.melquiades.model.trackers.Tracker;

public class SlowProcess implements Runnable {


    @Override
    public void run() {
        doRun();
    }

    @Operation(name = "slowRun", weight = 3)
    private void doRun(){
        try {
            Tracker tracker = Tracker.start();

            int value = 0;
            for (int i = 0; i < 100000; i++){
                if (value > 0) value -= i;
                if (value < 0) value += i;
                if (value == 0) value = -1000;
            }
            Thread.sleep(2000);

            tracker.finish();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
