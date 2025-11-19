/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Mock GitHub commit.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "repository", "hash" })
final class MkRepoCommit implements RepoCommit {

    /**
     * Commit SHA.
     */
    private final transient String hash;

    /**
     * The storage.
     */
    private final transient MkStorage storage;

    /**
     * The repository.
     */
    private final transient Repo repository;

    /**
     * Public ctor.
     * @param stg The storage
     * @param repo The repository
     * @param sha Commit SHA
     */
    MkRepoCommit(
        final MkStorage stg,
        final Repo repo,
        final String sha) {
        this.storage = stg;
        this.repository = repo;
        this.hash = sha;
    }

    @Override
    public int compareTo(
        final RepoCommit other
    ) {
        return new CompareToBuilder().append(
            this.repo().coordinates(),
            other.repo().coordinates()
        ).append(this.sha(), other.sha()).build();
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(
                String.format(
                    "/github/repos/repo[@coords='%s']/commits/commit[sha='%s']",
                        this.repo().coordinates(), this.hash
                )
            ).get(0)
        ).json();
    }

    @Override
    public Repo repo() {
        return this.repository;
    }

    @Override
    public String sha() {
        return this.hash;
    }

}
