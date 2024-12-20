package com.georgen.melquiades;

import com.georgen.melquiades.io.BufferReader;
import com.georgen.melquiades.util.Statistics;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;


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

            int bufferSize = 1024;
            BufferReader reader = new BufferReader("profiler.log", bufferSize);

            Path path = Paths.get("profiler.log");
            File file = path.toFile();

            long length = file.length();
            long iterations = length / bufferSize;
            int cursor = 2000;

            String line1 = reader.readLine(cursor);
            cursor += line1.length();
            String line2 = reader.readLine(cursor);
            cursor += line2.length();
            String line3 = reader.readLine(cursor);

            System.out.println(line1);
            System.out.println(line2);
            System.out.println(line3);

            List<Double> numbers = Arrays.asList(
                    0.456, 1.323, 2.0, 0.34, 0.67, 0.89, 1.2, 0.02, 0.3, 0.45,
                    1.0, 0.45, 0.657, 0.89, 0.34, 0.23, 0.77, 0.89, 0.9, 0.5
            );

            System.out.println(
                    "Numbers 50 percentile is " +
                            Statistics.percentile(numbers, 50)
            );

            System.out.println(
                    "Numbers 80 percentile is " +
                            Statistics.percentile(numbers, 80)
            );

            System.out.println(
                    "Numbers 95 percentile is " +
                    Statistics.percentile(numbers, 95)
            );

            System.out.println(
                    "Numbers mode is " +
                    Statistics.mode(numbers)
            );

            int threads = 3;
            int schedulerThreads = threads % 2 == 0 ? threads / 2 : threads / 2 + 1;
            int executorThreads = threads / 2;

            System.out.println("schedulerThreads: " + schedulerThreads);
            System.out.println("executorThreads: " + executorThreads);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}