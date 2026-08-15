/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;

/**
 * GitHub Search.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "ghub")
final class RtSearch implements Search {

    /**
     * Slash pattern for url splitting.
     */
    private static final Pattern SLASH = Pattern.compile("/");

    /**
     * Equals pattern for query splitting.
     */
    private static final Pattern QUERY = Pattern.compile("=");

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * RESTful Request to search.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req RESTful API entry point
     */
    RtSearch(final GitHub github, final Request req) {
        this(req.uri().path("/search").back(), github);
    }

    private RtSearch(final Request request, final GitHub ghub) {
        this.request = request;
        this.ghub = ghub;
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public Iterable<Repo> repos(
        final String keywords,
        final String sort,
        final Search.Order order) {
        return new RtSearchPagination<>(
            this.request, "repositories", keywords, sort, order.identifier(),
            object -> this.github().repos().get(
                new Coordinates.Simple(object.getString("full_name"))
            )
        );
    }

    @Override
    public Iterable<Issue> issues(final String keywords, final String sort,
        final Search.Order order, final Map<Search.Qualifier, String> qualifiers) {
        final StringBuilder keyword = new StringBuilder(keywords);
        for (final Map.Entry<Search.Qualifier, String> entry : qualifiers
            .entrySet()) {
            keyword.append('+').append(entry.getKey().identifier())
                .append(':').append(entry.getValue());
        }
        return new RtSearchPagination<>(
            this.request,
            "issues",
            keyword.toString(),
            sort,
            order.identifier(),
            object -> {
                try {
                    final String[] parts = RtSearch.SLASH.split(
                        new URI(object.getString("url")).getPath(), -1
                    );
                    return this.ghub.repos().get(
                        new Coordinates.Simple(parts[2], parts[3])
                    ).issues().get(object.getInt("number"));
                } catch (final URISyntaxException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
    }

    @Override
    public Iterable<User> users(
        final String keywords,
        final String sort,
        final Search.Order order) {
        return new RtSearchPagination<>(
            this.request, "users", keywords, sort, order.identifier(),
            object -> this.ghub.users().get(
                object.getString("login")
            )
        );
    }

    @Override
    public Iterable<Content> codes(
        final String keywords,
        final String sort,
        final Search.Order order) {
        return new RtSearchPagination<>(
            this.request, "code", keywords, sort, order.identifier(),
            object -> {
                try {
                    final URI uri = new URI(object.getString("url"));
                    final String[] parts = RtSearch.SLASH.split(
                        uri.getPath(), -1
                    );
                    return this.ghub.repos().get(
                        new Coordinates.Simple(parts[2], parts[3])
                    ).contents().get(
                        object.getString("path"),
                        RtSearch.QUERY.split(uri.getQuery(), -1)[1]
                    );
                } catch (final URISyntaxException | IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
    }
}
