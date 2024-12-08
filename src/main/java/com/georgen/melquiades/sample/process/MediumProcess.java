package com.georgen.melquiades.sample.process;

import com.georgen.melquiades.api.Operation;
import com.georgen.melquiades.model.Profiler;

public class MediumProcess implements Runnable {

    @Operation(name = "mediumRun", weight = 2)
    @Override
    public void run() {
        try {
            Profiler profiler = Profiler.start("MediumProcess.run()");

            int value = 0;
            for (int i = 0; i < 100000; i++){
                if (value > 0) value -= i;
                if (value < 0) value += i;
                if (value == 0) value = -1000;
            }
            Thread.sleep(500);
            profiler.finish();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
