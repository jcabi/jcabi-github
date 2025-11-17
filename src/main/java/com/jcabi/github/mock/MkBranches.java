/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Branch;
import com.jcabi.github.Branches;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Git branches.
 *
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
public final class MkBranches implements Branches {
    /**
     * XPath from a given branch to its commit SHA string.
     */
    private static final String XPATH_TO_SHA = "sha/text()";

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
     * @param login Username
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    MkBranches(
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
            ).addIf("branches")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Iterable<Branch> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/branch", this.xpath()),
            xml -> new MkBranch(
                this.storage,
                this.self,
                this.coords,
                xml.xpath("@name").get(0),
                xml.xpath(MkBranches.XPATH_TO_SHA).get(0)
            )
        );
    }

    @Override
    public Branch find(final String name) {
        throw new UnsupportedOperationException("find(name) not implemented");
    }

    /**
     * Creates a new branch.
     * @param name Name of branch
     * @param sha Commit SHA
     * @return New branch
     * @throws IOException if there is an I/O problem
     */
    public Branch create(
        final String name,
        final String sha)
        throws IOException {
        final Directives directives = new Directives()
            .xpath(this.xpath())
            .add("branch")
            .attr("name", name)
            .add("sha").set(sha).up();
        this.storage.apply(directives);
        return new MkBranch(this.storage, this.self, this.coords, name, sha);
    }

    /**
     * Gets a branch by name.
     * @param name Name of branch.
     * @return The branch with the given name
     * @throws IOException If there is an I/O problem
     */
    public Branch get(
        final String name
    ) throws IOException {
        return new MkBranch(
            this.storage,
            this.self,
            this.coords,
            name,
            this.storage.xml()
                .nodes(
                    String.format(
                        "%s/branch[@name='%s']",
                        this.xpath(),
                        name
                    )
                )
                .get(0)
                .xpath(MkBranches.XPATH_TO_SHA)
                .get(0)
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/branches",
            this.coords
        );
    }
}
