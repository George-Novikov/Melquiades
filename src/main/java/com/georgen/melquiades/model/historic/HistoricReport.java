package com.georgen.melquiades.model.historic;

import com.georgen.melquiades.model.data.DataRoot;

public abstract class HistoricReport {
    public abstract void consume(DataRoot data);
}
