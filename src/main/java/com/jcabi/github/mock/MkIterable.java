/**
 * Copyright (c) 2013-2014, JCabi.com
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Iterator;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock iterable.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
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
    MkIterable(@NotNull(message = "stg can't be NULL")
        final MkStorage stg,
        @NotNull(message = "path can't be NULL") final String path,
        @NotNull(message = "map can't be NULL") final MkIterable.Mapping<T> map
    ) {
        this.storage = stg;
        this.xpath = path;
        this.mapping = map;
    }

    @Override
    @NotNull(message = "Iterator is never NULL")
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
