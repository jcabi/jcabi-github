/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Blob;
import com.jcabi.github.Blobs;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.xembly.Directives;

/**
 * Mock GitHub blobs.
 * @since 0.5
 */
@Immutable
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkBlobs implements Blobs {

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
    MkBlobs(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this(MkBlobs.bootstrap(stg, rep), rep, login);
    }

    private MkBlobs(final MkStorage stg, final Coordinates rep, final String login) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Blob get(final String sha) {
        return new MkBlob(this.storage, sha, this.coords);
    }

    @Override
    public Blob create(
        final String content,
        final String encoding) throws IOException {
        this.storage.lock();
        final String sha = MkBlobs.fakeSha();
        try {
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("blob")
                    .add("sha").set(sha).up()
                    .add("url").set("http://localhost/1").up()
                    .attr("content", content)
                    .attr("encoding", encoding)
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(sha);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/blobs",
            this.coords
        );
    }

    /**
     * Generate a random fake SHA hex string.
     * @return Fake SHA string
     */
    private static String fakeSha() {
        return RandomStringUtils.secure().next(40, "0123456789abcdef");
    }

    /**
     * Prepare the storage.
     * @param stg Storage
     * @param rep Coordinates
     * @return The same storage
     * @throws IOException If fails
     */
    private static MkStorage bootstrap(final MkStorage stg, final Coordinates rep)
        throws IOException {
        stg.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/git",
                    rep
                )
            ).addIf("blobs")
        );
        return stg;
    }
}
