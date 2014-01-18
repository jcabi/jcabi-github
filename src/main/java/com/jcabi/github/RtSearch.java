/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.rexsl.test.Request;
import com.rexsl.test.RequestBody;
import com.rexsl.test.RequestURI;
import com.rexsl.test.Response;
import com.rexsl.test.Wire;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github Search.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle ClassDataAbstractionCoupling (2 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "ghub")
@SuppressWarnings("PMD.TooManyMethods")
public final class RtSearch implements Search {

    /**
     * Slash pattern for url splitting.
     */
    private static final Pattern SLASH = Pattern.compile("/");

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param github Github
     */
    RtSearch(final Github github) {
        this.ghub = github;
        this.entry = github.entry();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public Iterable<Repo> repos(
        @NotNull(message = "Search keywords can't be NULL")
        final String keywords,
        @NotNull(message = "Sort field can't be NULL") final String sort,
        @NotNull(message = "Sort order can't be NULL") final String order)
        throws IOException {
        return new RtPagination<Repo>(
            RtSearch.searchRequest(
                this.entry, "/search/repositories", keywords, sort, order
            ),
            new RtPagination.Mapping<Repo>() {
                @Override
                public Repo map(final JsonObject object) {
                    return new RtRepo(
                        RtSearch.this.ghub, RtSearch.this.entry,
                        new Coordinates.Simple(object.getString("full_name"))
                    );
                }
            }
        );
    }

    @Override
    public Iterable<Issue> issues(
        @NotNull(message = "Search keywords can't be NULL")
        final String keywords,
        @NotNull(message = "Sort field can't be NULL") final String sort,
        @NotNull(message = "Sort order can't be NULL") final String order)
        throws IOException {
        return new RtPagination<Issue>(
            RtSearch.searchRequest(
                this.entry, "/search/issues", keywords, sort, order
            ),
            // @checkstyle AnonInnerLength (21 lines)
            new RtPagination.Mapping<Issue>() {
                @Override
                public Issue map(final JsonObject object) {
                    try {
                        final String[] parts = RtSearch.SLASH.split(
                            new URI(object.getString("url")).getPath()
                        );
                        return new RtIssue(
                            RtSearch.this.entry,
                            new RtRepo(
                                RtSearch.this.ghub, RtSearch.this.entry,
                                // @checkstyle MagicNumber (1 line)
                                new Coordinates.Simple(parts[2], parts[3])
                            ),
                            object.getInt("number")
                        );
                    } catch (final URISyntaxException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }

    @Override
    public Iterable<User> users(
        @NotNull(message = "Search keywords can't be NULL")
        final String keywords,
        @NotNull(message = "Sort field can't be NULL") final String sort,
        @NotNull(message = "Sort order can't be NULL") final String order)
        throws IOException {
        return new RtPagination<User>(
            RtSearch.searchRequest(
                this.entry, "/search/users", keywords, sort, order
            ),
            new RtPagination.Mapping<User>() {
                @Override
                public User map(final JsonObject object) {
                    return new RtUser(
                        RtSearch.this.ghub, RtSearch.this.entry,
                        object.getString("login")
                    );
                }
            }
        );
    }

    /**
     * Build a search request.
     * @param req Base request
     * @param path Search path
     * @param keywords Search keywords
     * @param sort Sort field
     * @param order Sort order
     * @return Search request
     * @checkstyle ParameterNumber (4 lines)
     */
    @NotNull(message = "request is never NULL")
    private static Request searchRequest(final Request req, final String path,
        final String keywords, final String sort, final String order) {
        return new RtSearch.SearchRequest(
            req.uri().path(path)
                .queryParam("q", keywords)
                .queryParam("sort", sort)
                .queryParam("order", order)
                .back()
        );
    }

    /**
     * Request which hides everything but items.
     */
    @Immutable
    @SuppressWarnings({ "PMD.TooManyMethods", "PMD.CyclomaticComplexity" })
    private static final class SearchRequest implements Request {
        /**
         * Inner request.
         */
        private final transient Request request;
        /**
         * Ctor.
         * @param req Request to wrap
         */
        SearchRequest(@NotNull(message = "request can't be NULL")
            final Request req) {
            this.request = req;
        }
        @Override
        public RequestURI uri() {
            return this.request.uri();
        }
        @Override
        public RequestBody body() {
            return this.request.body();
        }
        @Override
        public Request header(@NotNull(message = "header name can't be NULL")
            final String name,
            @NotNull(message = "header value can't be NULL")
            final Object value) {
            return this.request.header(name, value);
        }
        @Override
        public Request reset(@NotNull(message = "header name can't be NULL")
            final String name) {
            return this.request.reset(name);
        }
        @Override
        public Request method(@NotNull(message = "method can't be NULL")
            final String method) {
            return this.request.method(method);
        }

        /**
         * Hide everything from the body but items.
         * @return Response
         * @throws IOException If any I/O problem occurs
         */
        @Override
        public Response fetch() throws IOException {
            final Response response = this.request.fetch();
            // @checkstyle AnonInnerLength (43 lines)
            return new Response() {
                @Override
                public Request back() {
                    return response.back();
                }
                @Override
                public int status() {
                    return response.status();
                }
                @Override
                public String reason() {
                    return response.reason();
                }
                @Override
                public Map<String, List<String>> headers() {
                    return response.headers();
                }
                @Override
                public String body() {
                    return Json.createReader(new StringReader(response.body()))
                        .readObject().getJsonArray("items").toString();
                }
                @Override
                public byte[] binary() {
                    return response.binary();
                }
                // @checkstyle MethodName (3 lines)
                @Override
                @SuppressWarnings("PMD.ShortMethodName")
                public <T> T as(final Class<T> type) {
                    try {
                        return type.getDeclaredConstructor(Response.class)
                            .newInstance(this);
                    } catch (final InstantiationException ex) {
                        throw new IllegalStateException(ex);
                    } catch (final IllegalAccessException ex) {
                        throw new IllegalStateException(ex);
                    } catch (final InvocationTargetException ex) {
                        throw new IllegalStateException(ex);
                    } catch (final NoSuchMethodException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            };
        }
        @Override
        public <T extends Wire> Request through(final Class<T> type,
            final Object... args) {
            return this.request.through(type, args);
        }
    }

}
