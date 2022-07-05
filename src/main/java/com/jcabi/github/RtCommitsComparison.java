/**
 * Copyright (c) 2013-2022, jcabi.com
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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Commits comparison.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtCommitsComparison implements CommitsComparison {

    /**
     * RESTful request for the comparison.
     */
    private final transient Request request;

    /**
     * Parent repository.
     */
    private final transient Repo owner;

    /**
     * Ctor.
     * @param req Entry point of API
     * @param repo Repository
     * @param base SHA of a base commit
     * @param head SHA of a head commit
     * @checkstyle ParameterNumber (3 lines)
     */
    RtCommitsComparison(final Request req, final Repo repo,
        final String base, final String head) {
        this.owner = repo;
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().toString())
            .path("/compare")
            .path(String.format("%s...%s", base, head))
            .back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<FileChange> files() throws IOException {
        return new FileChanges(this.json().getJsonArray("files"));
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    /**
     * Iterator that yields FileChange objects converted
     * from JSON objects in a JSON list.
     */
    @EqualsAndHashCode(of = { "iterator" })
    @ToString
    private static final class FileChangesIterator
        implements Iterator<FileChange> {
        /**
         * Encapsulated iterator of file change JSON objects.
         */
        private final transient Iterator<JsonObject> iterator;

        /**
         * Ctor.
         * @param iter Iterator of file change JSON objects
         */
        FileChangesIterator(
            final Iterator<JsonObject> iter
        ) {
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

    /**
     * Trivial iterable that returns FileChangesIterators using
     * the given JSON list.
     */
    @EqualsAndHashCode(of = { "list" })
    @Loggable(Loggable.DEBUG)
    @ToString
    private static final class FileChanges
        implements Iterable<FileChange> {
        /**
         * List of file change JSON objects.
         */
        private final transient List<JsonObject> list;

        /**
         * Ctor.
         * @param files JsonArray of file change objects
         */
        FileChanges(
            final JsonArray files
        ) {
            this.list = files.getValuesAs(JsonObject.class);
        }

        @Override
        public Iterator<FileChange> iterator() {
            return new FileChangesIterator(this.list.iterator());
        }
    }
}
