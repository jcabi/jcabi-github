/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Loggable;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Trivial iterable that returns FileChangesIterators using
 * the given JSON list.
 * @since 0.24
 */
@EqualsAndHashCode(of = "list")
@Loggable(Loggable.DEBUG)
@ToString
final class FileChanges
    implements Iterable<FileChange> {

    /**
     * List of file change JSON objects.
     */
    private final transient List<JsonObject> list;

    /**
     * Ctor.
     * @param files JsonArray of file change objects
     */
    FileChanges(final JsonArray files) {
        this(files.getValuesAs(JsonObject.class));
    }

    private FileChanges(final List<JsonObject> files) {
        this.list = files;
    }

    @Override
    public Iterator<FileChange> iterator() {
        return new FileChangesIterator(this.list.iterator());
    }
}
