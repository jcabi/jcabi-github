/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.RequestURI;
import java.net.URI;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * Wrapper of RequestURI that returns {@link SearchRequest}.
 * @since 0.4
 */
@Immutable
@EqualsAndHashCode(of = "address")
final class SearchUri implements RequestURI {

    /**
     * Underlying address.
     */
    private final transient RequestURI address;

    /**
     * Ctor.
     * @param uri The URI
     */
    SearchUri(final RequestURI uri) {
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
        return new SearchUri(this.address.set(uri));
    }

    @Override
    public RequestURI queryParam(final String name, final Object value) {
        return new SearchUri(this.address.queryParam(name, value));
    }

    @Override
    public RequestURI queryParams(final Map<String, String> map) {
        return new SearchUri(this.address.queryParams(map));
    }

    @Override
    public RequestURI path(final String segment) {
        return new SearchUri(this.address.path(segment));
    }

    @Override
    public RequestURI userInfo(final String info) {
        return new SearchUri(this.address.userInfo(info));
    }

    @Override
    public RequestURI port(final int num) {
        return new SearchUri(this.address.port(num));
    }
}
