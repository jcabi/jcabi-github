/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import jakarta.json.JsonValue;
import java.util.Iterator;
import lombok.EqualsAndHashCode;

/**
 * GitHub value pagination.
 * @param <T> Type of iterable objects
 * @param <P> Type of source objects
 * @see <a href="https://developer.github.com/v3/#pagination">Pagination</a>
 * @since 0.8
 */
@Immutable
@EqualsAndHashCode(of = { "entry", "map" })
public final class RtValuePagination<T, P extends JsonValue> implements
    Iterable<T> {

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
        final Request req,
        final RtValuePagination.Mapping<T, P> mpp
    ) {
        this.entry = req;
        this.map = mpp;
    }

    @Override
    public String toString() {
        return this.entry.uri().get().toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Items<>(this.entry, this.map);
    }

    /**
     * Entry.
     * @return Entry point
     */
    public Request request() {
        return this.entry;
    }

    /**
     * Mapping.
     * @return Mapping
     */
    public RtValuePagination.Mapping<T, P> mapping() {
        return this.map;
    }

    /**
     * Mapping from JsonValue successor to the destination type.
     * @param <X> Type of custom object
     * @param <P> Type of source object
     * @since 0.8
     */
    @Immutable
    @FunctionalInterface
    public interface Mapping<X, P extends JsonValue> {

        /**
         * Map JsonValue successor to the type required.
         * @param value Extends JsonValue
         * @return Custom object
         */
        X map(P value);
    }
}
