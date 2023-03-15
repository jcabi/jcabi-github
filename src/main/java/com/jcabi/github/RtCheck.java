package com.jcabi.github;

public class RtCheck implements Check {

    private final int id;
    private final String status;

    RtCheck(final int identifier, final String stat) {
        this.id = identifier;
        this.status = stat;
    }
}
