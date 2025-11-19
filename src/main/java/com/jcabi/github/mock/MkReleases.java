/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import com.jcabi.github.Repo;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub releases.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkReleases implements Releases {

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
    MkReleases(
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
            ).addIf("releases")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Iterable<Release> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/release", this.xpath()),
            xml -> this.get(
                Integer.parseInt(xml.xpath("id/text()").get(0))
            )
        );
    }

    @Override
    public Release get(final int number) {
        return new MkRelease(this.storage, this.self, this.coords, number);
    }

    @Override
    public Release create(
        final String tag
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                this.xpath().concat("/release/id/text()")
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("release")
                    .add("id").set(Integer.toString(number)).up()
                    .add("tag_name").set(tag).up()
                    .add("target_commitish").set("master").up()
                    .add("name").set("").up()
                    .add("body").set("").up()
                    .add("draft").set("true").up()
                    .add("prerelease").set("false").up()
                    .add("created_at").set(new GitHub.Time().toString()).up()
                    .add("published_at").set(new GitHub.Time().toString()).up()
                    .add("url").set("http://localhost/1").up()
                    .add("html_url").set("http://localhost/2").up()
                    .add("assets_url").set("http://localhost/3").up()
                    .add("upload_url").set("http://localhost/4").up()
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public void remove(final int number) throws IOException {
        this.storage.lock();
        try {
            this.storage.apply(
                new Directives().xpath(
                    String.format("%s/release[id='%d']", this.xpath(), number)
                ).remove()
            );
        } finally {
            this.storage.unlock();
        }
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/releases",
            this.coords
        );
    }
}
