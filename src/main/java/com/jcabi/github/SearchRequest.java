/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.RequestBody;
import com.jcabi.http.RequestURI;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import java.io.IOException;
import java.io.InputStream;

/**
 * Request which hides everything but items.
 * @since 0.4
 */
final class SearchRequest implements Request {

    /**
     * Inner request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request to wrap
     */
    SearchRequest(final Request req) {
        this.request = req;
    }

    @Override
    public RequestURI uri() {
        return new SearchUri(this.request.uri());
    }

    @Override
    public RequestBody body() {
        return this.request.body();
    }

    @Override
    public RequestBody multipartBody() {
        throw new UnsupportedOperationException("#multipart");
    }

    @Override
    public Request header(final String name, final Object value) {
        return new SearchRequest(this.request.header(name, value));
    }

    @Override
    public Request reset(final String name) {
        return new SearchRequest(this.request.reset(name));
    }

    @Override
    public Request method(final String method) {
        return new SearchRequest(this.request.method(method));
    }

    @Override
    public Request timeout(final int first, final int second) {
        return new SearchRequest(this.request);
    }

    @Override
    public Response fetch() throws IOException {
        return new Hidden(this.request.fetch());
    }

    @Override
    public Response fetch(final InputStream stream) throws IOException {
        return new Hidden(this.request.fetch(stream));
    }

    @Override
    public <T extends Wire> Request through(final Class<T> type,
        final Object... args) {
        return new SearchRequest(this.request.through(type, args));
    }

    @Override
    public Request through(final Wire wire) {
        throw new UnsupportedOperationException("#through");
    }
}
