package com.georgen.melquiades.model;

public class ProfilerGroup {

    public static final String DEFAULT_NAME = "default";
    public static final int DEFAULT_TYPE = 0;

    private String name;
    private int type;

    public ProfilerGroup() {
        this.name = DEFAULT_NAME;
    }
    public ProfilerGroup(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public ProfilerGroup(Profiler profiler){

    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }
}
