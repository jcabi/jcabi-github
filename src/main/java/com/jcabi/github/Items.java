/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.WebLinkingResponse;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import lombok.EqualsAndHashCode;

/**
 * Iterator.
 * @param <X> Type of custom object
 * @param <P> Type of source object
 * @since 0.8
 */
@EqualsAndHashCode(of = { "mapping", "request", "objects", "more" })
@SuppressWarnings("PMD.ConstructorShouldDoInitialization")
final class Items<X, P extends JsonValue> implements
    Iterator<X> {

    /**
     * Mapping to use.
     */
    private final transient RtValuePagination.Mapping<X, P> mapping;

    /**
     * Lock object.
     */
    private final transient ReentrantLock lock;

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
        this.objects = new ArrayDeque<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public X next() {
        this.lock.lock();
        try {
            if (!this.hasNext()) {
                throw new NoSuchElementException(
                    "no more elements in pagination, use #hasNext()"
                );
            }
            return this.mapping.map(this.objects.remove());
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("#remove()");
    }

    @Override
    public boolean hasNext() {
        this.lock.lock();
        try {
            if ((this.objects == null || this.objects.isEmpty())
                && this.more) {
                try {
                    this.fetch();
                } catch (final IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
            return !this.objects.isEmpty();
        } finally {
            this.lock.unlock();
        }
    }

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
        final Queue<P> list = new ArrayDeque<>();
        for (final JsonValue value : arr) {
            list.add((P) value);
        }
        this.objects = list;
    }
}
