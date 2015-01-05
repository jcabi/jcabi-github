package com.jcabi.github;

public class RtLanguage implements Language {

    private final String name;
    private final long bytes;

    public RtLanguage(String name, long bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public long bytes() {
        return bytes;
    }

}
