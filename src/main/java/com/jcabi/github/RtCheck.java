/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

/**
 * Github check.
 *
 * @see <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28">Check Runs API</a>
 * @since 1.5.0
 */
class RtCheck implements Check {

    /**
     * Status.
     */
    private final transient Status status;

    /**
     * Conclusion.
     */
    private final transient Conclusion conclusion;

    /**
     * Ctor.
     * @param stat Status.
     * @param conc Conclusion.
     */
    RtCheck(final String stat, final String conc) {
        this(Status.fromString(stat), Conclusion.fromString(conc));
    }

    /**
     * Ctor.
     * @param stat Status.
     * @param conc Conclusion.
     */
    RtCheck(
        final Status stat,
        final Conclusion conc
    ) {
        this.status = stat;
        this.conclusion = conc;
    }

    @Override
    public boolean successful() {
        return this.status.finished() && this.conclusion.successful();
    }
}
