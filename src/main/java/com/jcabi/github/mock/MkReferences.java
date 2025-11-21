/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Reference;
import com.jcabi.github.References;
import com.jcabi.github.Repo;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock of GitHub References.
 * @since 0.24
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class MkReferences implements References {

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
     * Public constructor.
     * @param stg Storage.
     * @param login Login name.
     * @param rep Repo coordinates.
     * @throws IOException - If something goes wrong.
     */
    MkReferences(
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
            ).addIf("refs")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Reference create(
        final String ref,
        final String sha
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath()).add("reference")
                .add("ref").set(ref).up()
                .add("sha").set(sha).up()
        );
        return this.get(ref);
    }

    @Override
    public Reference get(
        final String identifier
    ) {
        return new MkReference(
            this.storage, this.self, this.coords, identifier
        );
    }

    @Override
    public Iterable<Reference> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/reference"),
            xml -> this.get(
                xml.xpath("ref/text()").get(0)
            )
        );
    }

    @Override
    public Iterable<Reference> iterate(
        final String subnamespace
    ) {
        return new MkIterable<>(
            this.storage,
            String.format(
                "%s/reference/ref[starts-with(., 'refs/%s')]", this.xpath(),
                subnamespace
            ),
            xml -> this.get(
                xml.xpath("text()").get(0)
            )
        );
    }

    @Override
    public Iterable<Reference> tags() {
        return this.iterate("tags");
    }

    @Override
    public Iterable<Reference> heads() {
        return this.iterate("heads");
    }

    @Override
    public void remove(
        final String identifier
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "%s/reference[ref='%s']", this.xpath(), identifier
                )
            ).remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/refs",
            this.coords
        );
    }

}
