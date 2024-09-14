package com.georgen.melquiades.sample;

import com.georgen.melquiades.api.Operation;

import java.util.Arrays;

public class WorkerChain implements Runnable {
    private Runnable process;
    private WorkerChain child;

    public WorkerChain(Runnable... processes){
        if (processes != null && processes.length > 0) {
            this.process = processes[0];
            if (processes.length > 1) {
                this.child = new WorkerChain(Arrays.copyOfRange(processes, 1, processes.length));
            }
        }
    }

    @Operation(weight = 5)
    @Override
    public void run() {
        if (process != null) {
            process.run();
        }

        if (child != null) {
            child.run();
        }
    }
}
