/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Collaborators;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.User;
import java.io.IOException;
import org.apache.commons.lang3.NotImplementedException;
import org.xembly.Directives;

/**
 * Mock Github repository collaborators.
 *
 */
@Immutable
final class MkCollaborators implements Collaborators {

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
     * @param crds Coordinates
     * @throws IOException If there is any I/O problem
     */
    public MkCollaborators(
        final MkStorage stg,
        final String login,
        final Coordinates crds
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = crds;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']", this.coords
                )
            ).addIf("collaborators")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public boolean isCollaborator(
        final String user
    ) throws IOException {
        return !this.storage.xml().xpath(
            String.format("%s/user[login='%s']/text()", this.xpath(), user)
        ).isEmpty();
    }

    @Override
    public void add(
        final String user
    ) throws IOException {
        this.storage.lock();
        try {
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("user").add("login")
                    .set(user)
            );
        } finally {
            this.storage.unlock();
        }
    }

    @Override
    public void remove(
        final String user
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/user[login='%s']", this.xpath(), user)
            ).remove()
        );
    }

    @Override
    public Iterable<User> iterate() {
        return new MkIterable<>(
            this.storage, String.format("%s/user", this.xpath()),
            xml -> new MkUser(
                this.storage,
                xml.xpath("login/text()").get(0)
            )
        );
    }

    @Override
    public void addWithPermission(
        final String user, final Collaborators.Permission permission
    ) {
        throw new NotImplementedException("");
    }

    @Override
    public String permission(final String user) {
        return "write";
    }

    /**
     * Gets a mocked User.
     * @param login User login
     * @return Mocked User
     */
    public User get(
        final String login
    ) {
        return new MkUser(this.storage, login);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/collaborators",
            this.coords
        );
    }
}
