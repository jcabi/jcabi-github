/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Commits comparison.
 * @see <a href="https://developer.github.com/v3/repos/commits/#compare-two-commits">Compare two commits</a>
 * @since 0.5
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
     * @since 0.5
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
