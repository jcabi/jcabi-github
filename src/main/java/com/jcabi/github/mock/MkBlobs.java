/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
 * Mock Github blobs.
 *
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
     * @throws java.io.IOException If there is any I/O problem
     */
    public MkBlobs(
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
                    "/github/repos/repo[@coords='%s']/git",
                    this.coords
                )
            ).addIf("blobs")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }
    /**
     * Gets a mocked Blob.
     * @param sha Blob sha
     * @return Mocked Blob
     */
    public Blob get(
        final String sha) {
        return new MkBlob(this.storage, sha, this.coords);
    }
    @Override
    public Blob create(
        final String content,
        final String encoding)
        throws IOException {
        this.storage.lock();
        final String sha = fakeSha();
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
     *
     * @return Fake SHA string.
     */
    private static String fakeSha() {
        // @checkstyle MagicNumberCheck (1 line)
        return RandomStringUtils.random(40, "0123456789abcdef");
    }

}
