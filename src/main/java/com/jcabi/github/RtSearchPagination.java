/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.util.Iterator;
import lombok.EqualsAndHashCode;

/**
 * GitHub search pagination.
 * @param <T> Type of iterable objects
 * @since 0.4
 */
@Immutable
@EqualsAndHashCode
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
        this(
            req.uri().path(path)
                .queryParam("q", keywords)
                .queryParam("sort", sort)
                .queryParam("order", order)
                .back(),
            mppng
        );
    }

    private RtSearchPagination(
        final Request request,
        final RtValuePagination.Mapping<T, JsonObject> mapping
    ) {
        this.request = request;
        this.mapping = mapping;
    }

    @Override
    public Iterator<T> iterator() {
        return new RtPagination<>(
            new SearchRequest(this.request),
            this.mapping
        ).iterator();
    }
}
