/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Assignees;
import com.jcabi.github.Coordinates;
import com.jcabi.github.User;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Mock for GitHub Assignees.
 *
 * @since 0.7
 */
@Immutable
final class MkAssignees implements Assignees {

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
     */
    MkAssignees(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
    }

    @Override
    public Iterable<User> iterate() {
        final Set<User> assignees = new HashSet<>();
        assignees.add(new MkUser(this.storage, this.self));
        final Iterable<User> collabs = new MkIterable<>(
            this.storage,
            this.xpath().concat("/user"),
            xml -> new MkUser(
                this.storage,
                xml.xpath("login/text()").get(0)
            )
        );
        for (final User collab : collabs) {
            assignees.add(collab);
        }
        return assignees;
    }

    @Override
    public boolean check(
        final String login
    ) {
        try {
            final List<String> xpath = this.storage.xml().xpath(
                this.xpath().concat("/user/login/text()")
            );
            return this.self.equalsIgnoreCase(login) || !xpath.isEmpty()
                && StringUtils.equalsIgnoreCase(login, xpath.get(0));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * XPath of the Collaborators element in the XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/collaborators",
            this.coords
        );
    }
}
