/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.JsonObject;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Iterator that yields FileChange objects converted
 * from JSON objects in a JSON list.
 * @since 0.24
 */
@EqualsAndHashCode(of = "iterator")
@ToString
final class FileChangesIterator
    implements Iterator<FileChange> {

    /**
     * Encapsulated iterator of file change JSON objects.
     */
    private final transient Iterator<JsonObject> iterator;

    /**
     * Ctor.
     * @param iter Iterator of file change JSON objects
     */
    FileChangesIterator(final Iterator<JsonObject> iter) {
        this.iterator = iter;
    }

    @Override
    public FileChange next() {
        return new RtFileChange(this.iterator.next());
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("#remove()");
    }
}
