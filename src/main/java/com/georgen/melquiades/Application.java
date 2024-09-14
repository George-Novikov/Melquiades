package com.georgen.melquiades;

import com.georgen.melquiades.sample.WorkerChain;
import com.georgen.melquiades.sample.process.FastProcess;
import com.georgen.melquiades.sample.process.MediumProcess;
import com.georgen.melquiades.sample.process.SlowProcess;


public class Application {
    public static void main(String[] args) {
        try {
            WorkerChain chain = new WorkerChain(
                    new FastProcess(), new MediumProcess(), new SlowProcess()
            );

            chain.run();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}