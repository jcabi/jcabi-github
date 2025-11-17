/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Blobs;
import com.jcabi.github.Commits;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Git;
import com.jcabi.github.References;
import com.jcabi.github.Repo;
import com.jcabi.github.Tags;
import com.jcabi.github.Trees;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * GitHub Mock Git.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkGit implements Git {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    public MkGit(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']",
                    this.coords
                )
            ).addIf("git")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Blobs blobs() throws IOException {
        return new MkBlobs(this.storage, this.self, this.coords);
    }

    @Override
    public Commits commits() {
        try {
            return new MkCommits(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public References references() {
        try {
            return new MkReferences(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Tags tags() {
        try {
            return new MkTags(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Trees trees() {
        try {
            return new MkTrees(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
