package com.georgen.melquiades.core;

import com.georgen.melquiades.io.CloseableReader;
import com.georgen.melquiades.io.CloseableWriter;
import com.georgen.melquiades.model.data.DataRoot;
import com.georgen.melquiades.model.handlers.ErrorHandler;
import com.georgen.melquiades.model.historic.*;
import com.georgen.melquiades.model.settings.HistoryDepth;
import com.georgen.melquiades.model.settings.ProfilerSettings;
import com.georgen.melquiades.util.Serializer;
import com.georgen.melquiades.util.SystemHelper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class History {

    private HistoryReport report;
    private Path historyPath;
    private File historyFile;

    public History(ProfilerSettings settings) {
        initHistory(settings.getHistoryDepth());
    }

    public HistoryReport getReport() { return report; }

    public void update(DataRoot data) throws Exception {
        if (report == null || data == null || data.isEmpty()) return;
        report.consume(data);
        saveHistory();
    }

    private void saveHistory() throws Exception {
        if (report == null || historyPath == null || historyFile == null) return;

        if (!Files.exists(this.historyPath)){
            this.historyFile = SystemHelper.createFile(this.historyPath);
        }

        try (CloseableWriter writer = new CloseableWriter(historyFile, false)){
            writer.append(Serializer.serialize(report));
        }
    }

    //TODO: determine which history report type is about to be deserialized
    private void initHistory(HistoryDepth depth) {
        try {
            if (depth == null || HistoryDepth.NONE.equals(depth)) return;

            Class<? extends HistoryReport> javaClass = depth.getJavaClass();

            this.historyPath = Paths.get(Profiler.settings().getHomePath(), HistoryReport.FILE_NAME);
            this.historyFile = this.historyPath.toFile();

            if (!Files.exists(this.historyPath)){
                this.historyFile = SystemHelper.createFile(this.historyPath);
            }

            try (CloseableReader reader = new CloseableReader(this.historyFile)){
                String json = reader.read();
                if (json == null || json.isEmpty()) json = "{}";
                this.report = Serializer.deserialize(json, javaClass);
            }
        } catch (Exception e){
            ErrorHandler errorHandler = Profiler.getInstance().getErrorHandler();
            if (errorHandler != null) errorHandler.handle(e);
        }
    }
}


