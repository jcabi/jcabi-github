/**
 * Copyright (c) 2013-2014, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github Search.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
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
    @NotNull(message = "Github is never NULL")
    public Github github() {
        return this.ghub;
    }

    @Override
    @NotNull(message = "Iterable of repos is never NULL")
    public Iterable<Repo> repos(
        @NotNull(message = "Search keywords can't be NULL")
        final String keywords,
        @NotNull(message = "Sort field can't be NULL") final String sort,
        @NotNull(message = "Sort order can't be NULL") final String order)
        throws IOException {
        return new RtSearchPagination<Repo>(
            this.request, "repositories", keywords, sort, order,
            new RtPagination.Mapping<Repo, JsonObject>() {
                @Override
                public Repo map(final JsonObject object) {
                    return RtSearch.this.github().repos().get(
                        new Coordinates.Simple(object.getString("full_name"))
                    );
                }
            }
        );
    }

    @Override
    @NotNull(message = "Iterable of issues is never NULL")
    public Iterable<Issue> issues(
        @NotNull(message = "Search keywords can't be NULL")
        final String keywords,
        @NotNull(message = "Sort field can't be NULL") final String sort,
        @NotNull(message = "Sort order can't be NULL") final String order)
        throws IOException {
        return new RtSearchPagination<Issue>(
            this.request, "issues", keywords, sort, order,
            new RtPagination.Mapping<Issue, JsonObject>() {
                @Override
                public Issue map(final JsonObject object) {
                    try {
                        final String[] parts = RtSearch.SLASH.split(
                            new URI(object.getString("url")).getPath()
                        );
                        return RtSearch.this.ghub.repos().get(
                            // @checkstyle MagicNumber (1 line)
                            new Coordinates.Simple(parts[2], parts[3])
                        ).issues().get(object.getInt("number"));
                    } catch (final URISyntaxException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }

    @Override
    @NotNull(message = "Iterable of users is never NULL")
    public Iterable<User> users(
        @NotNull(message = "Search keywords can't be NULL")
        final String keywords,
        @NotNull(message = "Sort field can't be NULL") final String sort,
        @NotNull(message = "Sort order can't be NULL") final String order)
        throws IOException {
        return new RtSearchPagination<User>(
            this.request, "users", keywords, sort, order,
            new RtPagination.Mapping<User, JsonObject>() {
                @Override
                public User map(final JsonObject object) {
                    return RtSearch.this.ghub.users().get(
                        object.getString("login")
                    );
                }
            }
        );
    }

}
