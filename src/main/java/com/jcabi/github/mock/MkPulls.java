/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import com.jcabi.github.Pull;
import com.jcabi.github.Pulls;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub pull requests.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkPulls implements Pulls {
    /**
     * The separator between the username and
     * the branch name in the base/head parameters
     * when creating a pull request.
     */
    private static final String USER_BRANCH_SEP = ":";

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
    MkPulls(
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
            ).addIf("pulls")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Pull get(final int number) {
        return new MkPull(this.storage, this.self, this.coords, number);
    }

    @Override
    public Pull create(
        final String title,
        final String head,
        final String base
    ) throws IOException {
        if (head.isEmpty()) {
            throw new IllegalArgumentException("head cannot be empty!");
        }
        if (base.isEmpty()) {
            throw new IllegalArgumentException("base cannot be empty!");
        }
        final String canonical;
        if (head.contains(MkPulls.USER_BRANCH_SEP)) {
            canonical = head;
        } else {
            canonical = String.format(
                "%s%s%s",
                this.coords.user(),
                MkPulls.USER_BRANCH_SEP,
                head
            );
        }
        this.storage.lock();
        final int number;
        try {
            final Issue issue = this.repo().issues().create(title, "some body");
            number = issue.number();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("pull")
                    .add("number").set(Integer.toString(number)).up()
                    .add("head").set(canonical).up()
                    .add("base").set(base).up()
                    .add("checks").up()
                    .add("user")
                    .add("login").set(this.self)
                    .up()
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public Iterable<Pull> iterate(final Map<String, String> params) {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/pull"),
            xml -> this.get(
                Integer.parseInt(xml.xpath("number/text()").get(0))
            )
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/pulls",
            this.coords
        );
    }
}
