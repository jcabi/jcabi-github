/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Label;
import com.jcabi.github.Labels;
import com.jcabi.github.Repo;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github labels.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkLabels implements Labels {

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
    MkLabels(final MkStorage stg,
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
            ).addIf("labels")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Label get(final String name
    ) {
        return new MkLabel(this.storage, this.self, this.coords, name);
    }

    @Override
    public Label create(final String name,
        final String color
    ) throws IOException {
        if (!color.matches("[0-9a-f]{6}")) {
            throw new IllegalArgumentException(
                String.format(
                    "color '%s' is in wrong format, six hex letters expected",
                    color
                )
            );
        }
        this.storage.apply(
            new Directives().xpath(this.xpath()).add("label")
                .add("name").set(name).up()
                .add("color").set(color).up()
        );
        return this.get(name);
    }

    @Override
    public Iterable<Label> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/label", this.xpath()),
            xml -> this.get(
                xml.xpath("name/text()").get(0)
            )
        );
    }

    @Override
    public void delete(final String name
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath())
                .xpath(String.format("label[name='%s']", name))
                .remove()
                .xpath("/github/repos")
                .xpath(String.format("repo[@coords='%s']", this.coords))
                .xpath(String.format("issues/issue/labels/label[.='%s']", name))
                .remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/labels",
            this.coords
        );
    }
}
