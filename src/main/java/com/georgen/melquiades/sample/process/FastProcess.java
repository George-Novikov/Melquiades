package com.georgen.melquiades.sample.process;

import com.georgen.melquiades.api.Operation;
import com.georgen.melquiades.model.Profiler;

public class FastProcess implements Runnable {

    @Operation(name = "fastRun", weight = 1)
    @Override
    public void run() {
        Profiler profiler = Profiler.start("FastProcess", "run()");

        int value = 0;
        for (int i = 0; i < 100000; i++){
            if (value > 0) value -= i;
            if (value < 0) value += i;
            if (value == 0) value = -1000;
        }

        profiler.finish();
    }
}
