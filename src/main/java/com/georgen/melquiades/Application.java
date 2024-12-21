package com.georgen.melquiades;

import com.georgen.melquiades.io.BufferReader;
import com.georgen.melquiades.util.Statistics;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


public class Application {

    public static void main(String[] args) {

        try {
//            WorkerChain chain = new WorkerChain(
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess(),
//                    new FastProcess(), new FastProcess(), new FastProcess()
////                    new FastProcess(), new MediumProcess(), new SlowProcess()
//            );
//
//            chain.run();


            try (BufferReader reader = new BufferReader("profiler.log")) {
                long start = reader.findFirstPosition(0, "start=2024-12-08T22:58:13.623");
                long end = reader.findLastPosition(0, "start=2024-12-08T22:58:14.138");

                List<String> lines = reader.linesBetween(start, end);
                lines.forEach(System.out::println);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}