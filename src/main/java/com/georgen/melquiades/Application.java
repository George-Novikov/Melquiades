package com.georgen.melquiades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.io.BufferReader;
import com.georgen.melquiades.model.Hits;
import com.georgen.melquiades.model.data.DataCluster;
import com.georgen.melquiades.model.data.DataRoot;
import com.georgen.melquiades.model.settings.ProfilerSettings;
import com.georgen.melquiades.model.trackers.Tracker;
import com.georgen.melquiades.sample.WorkerChain;
import com.georgen.melquiades.sample.process.FastProcess;
import com.georgen.melquiades.sample.process.MediumProcess;
import com.georgen.melquiades.sample.process.SlowProcess;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Application {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void main(String[] args) {

        try {
            ProfilerSettings settings = Profiler.settings()
                    .enable();

            Profiler.launch(settings);

            System.out.println("Profiler is working: " + Profiler.isEnabled());
            Tracker globalTracker = Tracker.start("global", "Application", "main");

            WorkerChain chain = new WorkerChain(
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new MediumProcess(), new SlowProcess()
            );

            chain.run();

            chain = new WorkerChain(
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new MediumProcess(), new SlowProcess()
            );

            chain.run();

            DataRoot data = Profiler.getInstance().getData();

            chain = new WorkerChain(
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new MediumProcess(), new SlowProcess()
            );

            chain.run();

            globalTracker.finish();

            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            String json = mapper.writeValueAsString(data);
            System.out.println(json);
            DataRoot deserialized = mapper.readValue(json, DataRoot.class);
            System.out.println("Deserialized start: " + deserialized.getStart());
            System.out.println("Deserialized finish: " + deserialized.getFinish());

            Profiler.shutdown();

            LocalDateTime start = LocalDateTime.parse("2024-12-22T20:57:16", DATE_TIME_FORMATTER);
            LocalDateTime finish = LocalDateTime.parse("2024-12-22T22:11:30", DATE_TIME_FORMATTER);

            List<DataRoot> dataList = Profiler.report(start, finish);
            System.out.println(dataList.size());

            dataList.forEach(dl -> {
                System.out.println(dl.getDuration());
            });

            ConcurrentMap<String, DataCluster> clusters = dataList.get(0).getData();
            clusters.forEach((k, v) -> {
                System.out.println(v.getHits());
                System.out.println(v.getStat());
            });

            clusters = dataList.get(2).getData();
            clusters.forEach((k, v) -> {
                System.out.println(v.getHits());
                System.out.println(v.getStat());
            });


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}