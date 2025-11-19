/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock iterable.
 * @param <T> Type of iterable
 * @since 0.5
 */
@Loggable(Loggable.DEBUG)
@ToString
@Immutable
@EqualsAndHashCode(of = { "storage", "xpath", "mapping" })
final class MkIterable<T> implements Iterable<T> {

    /**
     * Storage to get XML from.
     */
    private final transient MkStorage storage;

    /**
     * XPath.
     */
    private final transient String xpath;

    /**
     * Mapping.
     */
    private final transient MkIterable.Mapping<T> mapping;

    /**
     * Public ctor.
     * @param stg Storage
     * @param path Path to search
     * @param map Mapping
     */
    MkIterable(final MkStorage stg,
        final String path,
        final MkIterable.Mapping<T> map
    ) {
        this.storage = stg;
        this.xpath = path;
        this.mapping = map;
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<XML> nodes;
        try {
            nodes = this.storage.xml().nodes(this.xpath).iterator();
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return nodes.hasNext();
            }

            @Override
            public T next() {
                return MkIterable.this.mapping.map(nodes.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("#remove()");
            }
        };
    }

    /**
     * Mapping.
     * @param <X> Type of item
     * @since 0.5
     */
    @Immutable
    public interface Mapping<X> {
        /**
         * Map from XML to X.
         * @param xml The XML to get it from
         * @return X
         */
        X map(XML xml);
    }

}
