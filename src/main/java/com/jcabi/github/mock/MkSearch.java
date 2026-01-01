/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Content;
import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Search;
import com.jcabi.github.User;
import java.util.EnumMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock GitHub search.
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkSearch implements Search {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Public ctor.
     *
     * @param stg Storage
     * @param login User to login
     */
    MkSearch(
        final MkStorage stg,
        final String login
    ) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    public GitHub github() {
        return new MkGitHub(this.storage, this.self);
    }

    @Override
    public Iterable<Repo> repos(
        final String keywords,
        final String sort,
        final Search.Order order
    ) {
        return new MkIterable<>(
            this.storage,
            "/github/repos/repo",
            xml -> new MkRepo(
                this.storage, this.self,
                new Coordinates.Simple(xml.xpath("@coords").get(0))
            )
        );
    }

    //@checkstyle ParameterNumberCheck (5 lines)
    @Override
    public Iterable<Issue> issues(final String keywords, final String sort,
        final Search.Order order, final EnumMap<Search.Qualifier, String> qualifiers
    ) {
        return new MkIterable<>(
            this.storage,
            "/github/repos/repo/issues/issue",
            xml -> new MkIssue(
                this.storage, this.self,
                new Coordinates.Simple(
                    xml.xpath("../../@coords").get(0)
                ),
                Integer.parseInt(xml.xpath("number/text()").get(0))
            )
        );
    }

    @Override
    public Iterable<User> users(
        final String keywords,
        final String sort,
        final Search.Order order
    ) {
        return new MkIterable<>(
            this.storage,
            "/github/users/user",
            xml -> new MkUser(
                this.storage,
                xml.xpath("login/text()").get(0)
            )
        );
    }

    @Override
    public Iterable<Content> codes(
        final String keywords,
        final String sort,
        final Search.Order order
    ) {
        return new MkIterable<>(
            this.storage,
            "/github/repos/repo/name",
            xml -> new MkContent(
                this.storage,
                this.self,
                new Coordinates.Simple(this.self, "repo"),
                "/path/to/search",
                "master"
            )
        );
    }

}
