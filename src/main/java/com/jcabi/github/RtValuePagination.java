/**
 * Copyright (c) 2013-2014, jcabi.com
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
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.WebLinkingResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github value pagination.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @param <T> Type of iterable objects
 * @param <P> Type of source objects
 * @see <a href="http://developer.github.com/v3/#pagination">Pagination</a>
 */
@Immutable
@EqualsAndHashCode(of = { "entry", "map" })
public class RtValuePagination<T, P extends JsonValue> implements Iterable<T> {

    /**
     * Mapping to use.
     */
    private final transient RtValuePagination.Mapping<T, P> map;

    /**
     * Start entry to use.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param req Request
     * @param mpp Mapping
     */
    public RtValuePagination(
        @NotNull(message = "req can't be NULL") final Request req,
        @NotNull(message = "map can't be null")
        final RtValuePagination.Mapping<T, P> mpp
    ) {
        this.entry = req;
        this.map = mpp;
    }

    @Override
    @NotNull(message = "toString is never NULL")
    public final String toString() {
        return this.entry.uri().get().toString();
    }

    @Override
    @NotNull(message = "Iterator is never NULL")
    public final Iterator<T> iterator() {
        return new RtValuePagination.Items<T, P>(this.entry, this.map);
    }

    /**
     * Entry.
     * @return Entry point
     */
    @NotNull(message = "Request is never NULL")
    public final Request request() {
        return this.entry;
    }

    /**
     * Mapping.
     * @return Mapping
     */
    @NotNull(message = "map is never NULLs")
    public final RtValuePagination.Mapping<T, P> mapping() {
        return this.map;
    }

    /**
     * Mapping from JsonValue successor to the destination type.
     * @param <X> Type of custom object
     * @param <P> Type of source object
     */
    @Immutable
    public interface Mapping<X, P extends JsonValue> {
        /**
         * Map JsonValue successor to the type required.
         * @param value Extends JsonValue
         * @return Custom object
         */
        X map(P value);
    }

    /**
     * Iterator.
     */
    @EqualsAndHashCode(of = { "mapping", "request", "objects", "more" })
    private static final class Items<X, P extends JsonValue> implements
        Iterator<X> {
        /**
         * Mapping to use.
         */
        private final transient RtValuePagination.Mapping<X, P> mapping;
        /**
         * Next entry to use.
         */
        private transient Request request;
        /**
         * Available objects.
         */
        private transient Queue<P> objects;
        /**
         * Current entry can be used to fetch objects.
         */
        private transient boolean more = true;
        /**
         * Ctor.
         * @param entry Entry
         * @param mpp Mapping
         */
        Items(final Request entry, final RtValuePagination.Mapping<X, P> mpp) {
            this.request = entry;
            this.mapping = mpp;
            this.objects = new LinkedList<P>();
        }
        @Override
        public X next() {
            synchronized (this.mapping) {
                if (!this.hasNext()) {
                    throw new NoSuchElementException(
                        "no more elements in pagination, use #hasNext()"
                    );
                }
                return this.mapping.map(this.objects.remove());
            }
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("#remove()");
        }
        @Override
        public boolean hasNext() {
            synchronized (this.mapping) {
                if ((this.objects == null || this.objects.isEmpty())
                    && this.more) {
                    try {
                        this.fetch();
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
                return !this.objects.isEmpty();
            }
        }
        /**
         * Fetch the next portion, if available.
         * @throws IOException If there is any I/O problem
         */
        @SuppressWarnings("unchecked")
        private void fetch() throws IOException {
            final RestResponse response = this.request.fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK);
            final WebLinkingResponse.Link link = response
                .as(WebLinkingResponse.class)
                .links()
                .get("next");
            if (link == null) {
                this.more = false;
            } else {
                this.request = response.jump(link.uri());
            }
            final JsonArray arr = response.as(JsonResponse.class).json()
                .readArray();
            final Queue<P> list = new LinkedList<P>();
            for (final JsonValue value : arr) {
                list.add((P) value);
            }
            this.objects = list;
        }
    }

}
