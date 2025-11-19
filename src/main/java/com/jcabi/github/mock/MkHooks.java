/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Hook;
import com.jcabi.github.Hooks;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub hooks.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkHooks implements Hooks {

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
    MkHooks(
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
            ).addIf("hooks")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Iterable<Hook> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/hook"),
            xml -> this.get(
                Integer.parseInt(xml.xpath("id/text()").get(0))
            )
        );
    }

    @Override
    public Hook get(final int number) {
        return new MkHook(this.storage, this.self, this.coords, number);
    }

    // @checkstyle ParameterNumberCheck (2 lines)
    @Override
    public Hook create(
        final String name,
        final Map<String, String> config,
        final Iterable<Event> events, final boolean active
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                this.xpath().concat("/hook/id/text()")
            ).size();
            final Directives dirs = new Directives().xpath(this.xpath())
                .add("hook")
                .add("id").set(String.valueOf(number)).up()
                .add("name").set(name).up()
                .add("active").set(Boolean.toString(active)).up()
                .add("events");
            for (final Event event : events) {
                dirs.add("event").set(event.toString()).up();
            }
            dirs.up()
                .add("config");
            for (final Map.Entry<String, String> entr : config.entrySet()) {
                dirs.add(entr.getKey()).set(entr.getValue()).up();
            }
            this.storage.apply(dirs);
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public void remove(final int number) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                this.xpath().concat(String.format("/hook[id='%d']", number))
            ).remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/hooks",
            this.coords
        );
    }

}
