/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.util.Iterator;

/**
 * GitHub pagination.
 * <p>This class is a convenient iterator over multiple JSON objects
 * returned by GitHub API. For example, to iterate through notifications
 * (see Notifications API) you can use this code:</p>
 * <pre> Iterable&lt;JsonObject&gt; notifications = new RtPagination&lt;&gt;(
 *   new RtGitHub(oauth).entry()
 *     .uri().path("/notifications").back(),
 *   RtPagination.COPYING
 * );</pre>
 * @param <T> Type of iterable objects
 * @see <a href="https://developer.github.com/v3/#pagination">Pagination</a>
 * @since 0.11
 */
@Immutable
public final class RtPagination<T> implements Iterable<T> {

    /**
     * Mapping that just copies JsonObject.
     * @checkstyle LineLength (3 lines)
     */
    public static final RtValuePagination.Mapping<JsonObject, JsonObject> COPYING =
        value -> value;

    /**
     * Encapsulated paging.
     */
    private final transient RtValuePagination<T, JsonObject> pages;

    /**
     * Public ctor.
     * @param req Request
     * @param mpp Mapping
     */
    public RtPagination(
        final Request req,
        final RtValuePagination.Mapping<T, JsonObject> mpp
    ) {
        this.pages = new RtValuePagination<>(req, mpp);
    }

    @Override
    public Iterator<T> iterator() {
        return this.pages.iterator();
    }

    /**
     * Entry.
     * @return Entry point
     */
    public Request request() {
        return this.pages.request();
    }

    /**
     * Mapping.
     * @return Mapping
     */
    public RtValuePagination.Mapping<T, JsonObject> mapping() {
        return this.pages.mapping();
    }
}
