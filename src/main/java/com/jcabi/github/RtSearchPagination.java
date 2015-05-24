/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.http.Request;
import com.jcabi.http.RequestBody;
import com.jcabi.http.RequestURI;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github search pagination.
 *
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @param <T> Type of iterable objects
 */
@Immutable
@EqualsAndHashCode
@SuppressWarnings("PMD.TooManyMethods")
final class RtSearchPagination<T> implements Iterable<T> {

    /**
     * Search request.
     */
    private final transient Request request;

    /**
     * Pagination mapping.
     */
    private final transient RtValuePagination.Mapping<T, JsonObject> mapping;

    /**
     * Ctor.
     * @param req RESTful API entry point
     * @param path Search path
     * @param keywords Search keywords
     * @param sort Sort field
     * @param order Sort order
     * @param mppng Pagination mapping
     * @checkstyle ParameterNumber (4 lines)
     */
    RtSearchPagination(final Request req, final String path,
        final String keywords, final String sort, final String order,
        final RtValuePagination.Mapping<T, JsonObject> mppng) {
        this.request = req.uri().path(path)
            .queryParam("q", keywords)
            .queryParam("sort", sort)
            .queryParam("order", order)
            .back();
        this.mapping = mppng;
    }

    @Override
    @NotNull(message = "Iterator is never NULL")
    public Iterator<T> iterator() {
        return new RtPagination<T>(
            new RtSearchPagination.SearchRequest(this.request),
            this.mapping
        ).iterator();
    }

    /**
     * Request which hides everything but items.
     */
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
        @NotNull(message = "Request isn't ever NULL")
        public RequestURI uri() {
            return new SearchURI(this.request.uri());
        }
        @Override
        @NotNull(message = "body is never NULL")
        public RequestBody body() {
            return this.request.body();
        }
        @Override
        @NotNull(message = "header is nerver NULL")
        public Request header(@NotNull(message = "header name can't be NULL")
            final String name, @NotNull(message = "header value can't be NULL")
            final Object value) {
            return new SearchRequest(this.request.header(name, value));
        }
        @Override
        @NotNull(message = "Request is nerver NULL")
        public Request reset(@NotNull(message = "header name can't be NULL")
            final String name) {
            return new SearchRequest(this.request.reset(name));
        }
        @Override
        @NotNull(message = "Request is never NULL")
        public Request method(@NotNull(message = "method can't be NULL")
            final String method) {
            return new SearchRequest(this.request.method(method));
        }
        /**
         * Hide everything from the body but items.
         * @return Response
         * @throws IOException If any I/O problem occurs
         */
        @Override
        @NotNull(message = "Response is never NULL")
        public Response fetch() throws IOException {
            return new RtSearchPagination.Hidden(this.request.fetch());
        }
        @Override
        public Response fetch(final InputStream stream) throws IOException {
            return new RtSearchPagination.Hidden(this.request.fetch(stream));
        }
        @Override
        @NotNull(message = "Request should never be NULL")
        public <T extends Wire> Request through(final Class<T> type,
            final Object... args) {
            return new SearchRequest(this.request.through(type, args));
        }
    }

    /**
     * Response to return.
     */
    @Immutable
    private static final class Hidden implements Response {
        /**
         * Original response.
         */
        private final transient Response response;
        /**
         * Ctor.
         * @param resp Response
         */
        Hidden(final Response resp) {
            this.response = resp;
        }
        @Override
        @NotNull(message = "Request cannot be NULL")
        public Request back() {
            return new SearchRequest(this.response.back());
        }
        @Override
        public int status() {
            return this.response.status();
        }
        @Override
        @NotNull(message = "reason is never NULL")
        public String reason() {
            return this.response.reason();
        }
        @Override
        @NotNull(message = "headers is never NULL")
        public Map<String, List<String>> headers() {
            return this.response.headers();
        }
        @Override
        @NotNull(message = "body is never NULL")
        public String body() {
            return Json.createReader(new StringReader(this.response.body()))
                .readObject().getJsonArray("items").toString();
        }
        @Override
        public byte[] binary() {
            try {
                return this.body().getBytes("UTF-8");
            } catch (final UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
        // @checkstyle MethodName (4 lines)
        @Override
        @SuppressWarnings("PMD.ShortMethodName")
        @NotNull(message = "T is never NULL")
        public <T extends Response> T as(final Class<T> type) {
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
    }

    /**
     * Wrapper of RequestURI that returns {@link SearchRequest}.
     */
    @Immutable
    @EqualsAndHashCode(of = "address")
    private static final class SearchURI implements RequestURI {
        /**
         * Underlying address.
         */
        private final transient RequestURI address;
        /**
         * Ctor.
         * @param uri The URI
         */
        public SearchURI(final RequestURI uri) {
            this.address = uri;
        }
        @Override
        public Request back() {
            return new SearchRequest(this.address.back());
        }
        @Override
        public URI get() {
            return this.address.get();
        }
        @Override
        public RequestURI set(final URI uri) {
            return new SearchURI(this.address.set(uri));
        }
        @Override
        public RequestURI queryParam(final String name, final Object value) {
            return new SearchURI(this.address.queryParam(name, value));
        }
        @Override
        public RequestURI queryParams(final Map<String, String> map) {
            return new SearchURI(this.address.queryParams(map));
        }
        @Override
        public RequestURI path(final String segment) {
            return new SearchURI(this.address.path(segment));
        }
        @Override
        public RequestURI userInfo(final String info) {
            return new SearchURI(this.address.userInfo(info));
        }
        @Override
        public RequestURI port(final int num) {
            return new SearchURI(this.address.port(num));
        }
    }
}
