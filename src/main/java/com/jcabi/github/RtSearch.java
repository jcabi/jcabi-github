/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;

/**
 * Github Search.
 *
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "ghub")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful Request to search.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param github Github
     * @param req RESTful API entry point
     */
    RtSearch(final Github github, final Request req) {
        this.ghub = github;
        this.request = req.uri().path("/search").back();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public Iterable<Repo> repos(
        final String keywords,
        final String sort,
        final Order order) {
        return new RtSearchPagination<>(
            this.request, "repositories", keywords, sort, order.identifier(),
            object -> this.github().repos().get(
                new Coordinates.Simple(object.getString("full_name"))
            )
        );
    }

    //@checkstyle ParameterNumberCheck (5 lines)
    @Override
    public Iterable<Issue> issues(final String keywords, final String sort,
        final Order order, final EnumMap<Qualifier, String> qualifiers) {
        final StringBuilder keyword = new StringBuilder(keywords);
        for (final EnumMap.Entry<Qualifier, String> entry : qualifiers
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
                        // @checkstyle MultipleStringLiteralsCheck (1 line)
                        new URI(object.getString("url")).getPath()
                    );
                    return this.ghub.repos().get(
                        // @checkstyle MagicNumber (1 line)
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
        final Order order) {
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
        final Order order) {
        return new RtSearchPagination<>(
            this.request, "code", keywords, sort, order.identifier(),
            // @checkstyle AnonInnerLengthCheck (25 lines)
            object -> {
                try {
                    // @checkstyle MultipleStringLiteralsCheck (1 line)
                    final URI uri = new URI(object.getString("url"));
                    final String[] parts = RtSearch.SLASH.split(
                        uri.getPath()
                    );
                    final String ref = RtSearch.QUERY.split(
                        uri.getQuery()
                    )[1];
                    return this.ghub.repos().get(
                        // @checkstyle MagicNumber (1 line)
                        new Coordinates.Simple(parts[2], parts[3])
                    ).contents().get(object.getString("path"), ref);
                } catch (final URISyntaxException ex) {
                    throw new IllegalStateException(ex);
                } catch (final IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        );
    }

}
