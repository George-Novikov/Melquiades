Initialization:
```java
Profiler.settings()
    .enable()
    .launch();
```

Also:
```java
ProfilerSettings settings = ProfilerSettings.getDefault()
        .threads(2)
        .historyDepth(HistoryDepth.WEEK)
        .enable()
        .launch();

Profiler.launch(settings);
```

Use `Tracker` interface to start and stop measuring process time:
```java
Tracker tracker = Tracker.start("general", "FastProcess", "run()");

// your code here

tracker.finish(); // sends data automatically
```

Or create a flexible wrapper for your business logic:
```java
public class CustomTracker {
    public static Tracker start(String group, String process){
        return Tracker.start("Custom", group, process);
    }
}

Tracker tracker = CustomTracker.start("YourProcess", "doSomething()");
...
tracker.finish();
```

Available fields within ProfilerSettings (JSON for simplicity):
```json
{
  "enabled": true,
  "threads": 2,
  "interval": 1000,
  "homePath": "/your/home/path",
  "fileName": "profiler.jsonl",
  "emptyWrite": true,
  "metrics": ["avg", "p50", "p75", "p95"],
  "logging": {
    "root": ["HITS", "STAT", "DATA"],
    "clusters": ["HITS", "STAT", "DATA"],
    "groups": ["HITS", "STAT", "DATA"],
    "processes": ["HITS", "STAT", "DATA"]
  },
  "blacklist": {
    "clusters": null,
    "groups": null,
    "processes": null
  },
  "historyDepth": "WEEK"
}
```



Shutdown:
```java
Profiler.shutdown();
```

Loading report from data accumulated in the `profiler.jsonl` file:
```java
LocalDateTime start = LocalDateTime.parse("2024-12-29T09:34:14", DATE_TIME_FORMATTER);
LocalDateTime finish = LocalDateTime.parse("2024-12-29T12:25:02", DATE_TIME_FORMATTER);
List<DataRoot> dataList = Profiler.report(start, finish);
```

Analyzing intersections between clusters, groups, processes:
```java
DataRoot dataRoot = dataList.get(0);
DataCluster dataCluster1 = dataRoot.getData().get("general");
DataCluster dataCluster2 = dataRoot.getData().get("global");
List<Intersection> intersections1 = Intersection.of(dataRoot, "p50");
List<Intersection> intersections2 = Intersection.of(dataCluster1, "p75");
List<Intersection> intersections3 = Intersection.of(dataCluster2, "avg");
```