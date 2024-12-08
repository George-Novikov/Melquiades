package com.georgen.melquiades;

import com.georgen.melquiades.io.BufferedAppender;
import com.georgen.melquiades.io.BufferedReader;
import com.georgen.melquiades.sample.WorkerChain;
import com.georgen.melquiades.sample.process.FastProcess;
import com.georgen.melquiades.sample.process.MediumProcess;
import com.georgen.melquiades.sample.process.SlowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.nio.file.Paths;


public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final Logger LOGGER_TWO = LoggerFactory.getLogger("TWO");

    public static void main(String[] args) {

        LOGGER.debug("Start");
        LOGGER_TWO.info("How are you?");

        try {

            BufferedAppender appender = new BufferedAppender("log-folder/my-file");

            for (int x = 0; x < 10; x++) {
                appender.append("Some message");
                appender.append("Some another message");
                appender.append("And some another message");
            }

            BufferedReader reader = new BufferedReader("log-folder/my-file");

            int cursor = 0;
            String line1 = reader.readLine(cursor);
            cursor += line1.length();
            String line2 = reader.readLine(cursor);
            cursor += line2.length();
            String line3 = reader.readLine(cursor);

            System.out.println(line1);
            System.out.println(line2);
            System.out.println(line3);

            WorkerChain chain = new WorkerChain(
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new MediumProcess(), new SlowProcess()
            );

            chain.run();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}