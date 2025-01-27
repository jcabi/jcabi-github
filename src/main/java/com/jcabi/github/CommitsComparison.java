/**
 * Copyright (c) 2013-2025, jcabi.com
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Commits comparison.
 * @see <a href="https://developer.github.com/v3/repos/commits/#compare-two-commits">Compare two commits</a>
 */
@Immutable
public interface CommitsComparison extends JsonReadable {

    /**
     * Get a parent repository of commits.
     * @return Repository
     */
    Repo repo();

    /**
     * Iterate over the file changes between the two commits being
     * compared.
     * @return Iterable of file changes
     * @throws IOException If there is any I/O problem
     */
    Iterable<FileChange> files() throws IOException;

    /**
     * Smart commits comparison with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "comparison")
    final class Smart implements CommitsComparison {

        /**
         * Encapsulated commits comparison.
         */
        private final transient CommitsComparison comparison;

        /**
         * Public ctor.
         * @param cmprsn Commits comparison
         */
        public Smart(final CommitsComparison cmprsn) {
            this.comparison = cmprsn;
        }

        /**
         * Get commits.
         * @return Commits
         * @throws IOException If there is any I/O problem
         */
        public Iterable<RepoCommit> commits() throws IOException {
            final JsonArray array = this.comparison.json()
                .getJsonArray("commits");
            final Collection<RepoCommit> commits =
                new ArrayList<>(array.size());
            final RepoCommits repo = this.comparison.repo().commits();
            for (final JsonValue value : array) {
                commits.add(
                    repo.get(JsonObject.class.cast(value).getString("sha"))
                );
            }
            return commits;
        }

        @Override
        public Iterable<FileChange> files() throws IOException {
            return this.comparison.files();
        }

        @Override
        public Repo repo() {
            return this.comparison.repo();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.comparison.json();
        }
    }
}
