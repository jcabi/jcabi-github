/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Fork;
import com.jcabi.github.Forks;
import com.jcabi.github.Repo;
import com.jcabi.log.Logger;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock GitHub forks.
 * @since 0.8
 */
@Immutable
@EqualsAndHashCode(of = { "storage", "self", "coords" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class MkForks implements Forks {

    /**
     * XPath suffix for fork ID text.
     */
    private static final String FORK_ID_TEXT_PATH = "/fork/id/text()";

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
    MkForks(
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
            ).addIf("forks")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    /**
     * Gets a mocked Fork.
     * @param forkid Fork id
     * @return Mocked Fork
     */
    public Fork get(final int forkid) {
        return new MkFork(this.storage, forkid, this.coords);
    }

    @Override
    public Iterable<Fork> iterate(
        final String sort
    ) {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/fork"),
            xml -> this.get(
                Integer.parseInt(xml.xpath("id/text()").get(0))
            )
        );
    }

    @Override
    public Fork create(
        final String org
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                this.xpath().concat(MkForks.FORK_ID_TEXT_PATH)
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("fork")
                    .add("id").set(Integer.toString(number)).up()
                    .attr("organization", org)
            );
        } finally {
            this.storage.unlock();
        }
        Logger.info(
            this, "fork %s created inside %s by %s",
            this.coords, org, this.self
        );
        return this.get(number);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/forks",
            this.coords
        );
    }
}
