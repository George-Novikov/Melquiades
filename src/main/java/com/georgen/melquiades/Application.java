package com.georgen.melquiades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georgen.melquiades.core.Profiler;
import com.georgen.melquiades.core.ReportBuilder;
import com.georgen.melquiades.io.BufferReader;
import com.georgen.melquiades.model.Intersection;
import com.georgen.melquiades.model.data.DataCluster;
import com.georgen.melquiades.model.data.DataRoot;
import com.georgen.melquiades.model.settings.HistoryDepth;
import com.georgen.melquiades.model.settings.ProfilerSettings;
import com.georgen.melquiades.core.trackers.Tracker;
import com.georgen.melquiades.sample.WorkerChain;
import com.georgen.melquiades.sample.process.FastProcess;
import com.georgen.melquiades.sample.process.MediumProcess;
import com.georgen.melquiades.sample.process.SlowProcess;
import com.georgen.melquiades.util.Serializer;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Application {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void main(String[] args) {

        try {
            ProfilerSettings settings = ProfilerSettings.getDefault()
                    .threads(2)
                    .emptyWrite(false)
                    .historyDepth(HistoryDepth.WEEK)
                    .enable()
                    .launch();

            System.out.println(Serializer.serialize(settings));

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

            chain = new WorkerChain(
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new FastProcess(), new FastProcess(),
                    new FastProcess(), new MediumProcess(), new SlowProcess()
            );

            chain.run();

            globalTracker.finish();

            Profiler.shutdown();

            LocalDateTime start = LocalDateTime.parse("2024-12-29T12:27:33", DATE_TIME_FORMATTER);
            LocalDateTime finish = LocalDateTime.parse("2024-12-29T12:28:01", DATE_TIME_FORMATTER);

            List<DataRoot> dataList = Profiler.report(start, finish, 2);
            System.out.println(dataList.size());
            if (dataList.isEmpty()) return;

            dataList.forEach(dl -> {
                System.out.println(dl.getDuration());
            });

            DataRoot dataRoot = dataList.get(0);
            DataCluster dataCluster1 = dataRoot.getData().get("general");
            DataCluster dataCluster2 = dataRoot.getData().get("global");

            List<Intersection> intersections1 = Intersection.of(dataRoot, "p50");
            System.out.println(
                    Serializer.serialize(intersections1)
            );

            List<Intersection> intersections2 = Intersection.of(dataCluster1, "p75");
            System.out.println(
                    Serializer.serialize(intersections2)
            );

            List<Intersection> intersections3 = Intersection.of(dataCluster2, "p95");
            System.out.println(
                    Serializer.serialize(intersections3)
            );


//            ConcurrentMap<String, DataCluster> clusters = dataRoot.getData();
//            clusters.forEach((k, v) -> {
//                System.out.println(v.getHits());
//                System.out.println(v.getStat());
//            });
//
//            clusters = dataList.get(2).getData();
//            clusters.forEach((k, v) -> {
//                System.out.println(v.getHits());
//                System.out.println(v.getStat());
//            });


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}