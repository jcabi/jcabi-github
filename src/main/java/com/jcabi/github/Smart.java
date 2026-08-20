/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * Smart RepositoryStatistics.
 * @since 1.8.0
 */
public final class Smart {

    /**
     * Repository statistics.
     */
    private final transient RepositoryStatistics stats;

    /**
     * Public ctor.
     * @param repo Repository
     */
    public Smart(final Repo repo) {
        this(new RepositoryStatistics(repo));
    }

    /**
     * Public ctor.
     * @param statistics Repository statistics
     */
    public Smart(final RepositoryStatistics statistics) {
        this.stats = statistics;
    }

    /**
     * Number of forks of this repository.
     * @return Number of forks
     * @throws IOException If there is any I/O problem
     */
    public int forks() throws IOException {
        return this.integer(RepositoryStatistics.Key.FORKS_COUNT);
    }

    /**
     * Number of users who have starred this repository.
     * @return Number of stargazers
     * @throws IOException If there is any I/O problem
     */
    public int stargazers() throws IOException {
        return this.integer(RepositoryStatistics.Key.STARGAZERS_COUNT);
    }

    /**
     * Number of users watching the repository.
     * @return Number of watchers
     * @throws IOException If there is any I/O problem
     */
    public int watchers() throws IOException {
        return this.integer(RepositoryStatistics.Key.WATCHERS_COUNT);
    }

    /**
     * The size of the repository.
     * @return Size of the repository
     * @throws IOException If there is any I/O problem
     */
    public int size() throws IOException {
        return this.integer(RepositoryStatistics.Key.SIZE);
    }

    /**
     * The number of open issues in this repository.
     * @return Number of open issues
     * @throws IOException If there is any I/O problem
     */
    public int openIssues() throws IOException {
        return this.integer(RepositoryStatistics.Key.OPEN_ISSUES_COUNT);
    }

    /**
     * The time the repository was created.
     * @return Time the repository was created
     * @throws IOException If there is any I/O problem
     */
    public ZonedDateTime created() throws IOException {
        return this.datetime(RepositoryStatistics.Key.CREATED_AT);
    }

    private int integer(final RepositoryStatistics.Key key) throws IOException {
        return Integer.parseInt(
            String.valueOf(this.stats.toMap().get(key.getKey()))
        );
    }

    private ZonedDateTime datetime(final RepositoryStatistics.Key key) throws IOException {
        return ZonedDateTime.parse(
            String.valueOf(this.stats.toMap().get(key.getKey()))
        );
    }
}
