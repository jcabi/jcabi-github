/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * Repository statistics.
 *
 * @since 1.8.0
 */
public final class RepositoryStatistics {

    /**
     * Repository.
     */
    private final transient Repo repo;

    /**
     * Public ctor.
     * @param repository Repository
     */
    public RepositoryStatistics(final Repo repository) {
        this.repo = repository;
    }

    /**
     * Get all statistics as a map.
     * @return Map of statistics
     * @throws IOException If there is any I/O problem
     */
    public Map<String, Object> toMap() throws IOException {
        final JsonObject json = this.repo.json();
        return Arrays.stream(KEY.values())
            .collect(
                Collectors.toMap(
                    key -> key.key,
                    key -> key.value(json)
                )
            );
    }

    /**
     * Keys of the JSON object returned by the GitHub API.
     *
             * @since 1.8.0
     */
    private enum KEY {

        /**
         * The main programming language used in the repository.
         * Type: string. Might be null.
         */
        LANGUAGE("language"),

        /**
         * Number of forks of this repository.
         * Type: integer.
         */
        FORKS_COUNT("forks_count"),

        /**
         * Number of users who have starred this repository.
         * Type: integer.
         */
        STARGAZERS_COUNT("stargazers_count"),

        /**
         * Number of users watching the repository.
         * Type: integer.
         */
        WATCHERS_COUNT("watchers_count"),

        /**
         * The size of the repository. Size is calculated hourly.
         * When a repository is initially created, the size is 0.
         * Type: integer.
         */
        SIZE("size"),

        /**
         * Number of open issues in this repository.
         * Type: integer.
         */
        OPEN_ISSUES_COUNT("open_issues_count"),

        /**
         * The date and time the repository was created.
         * Type: string. Might be null.
         * Format: date-time.
         * Example: "2011-01-26T19:01:12Z".
         */
        CREATED_AT("created_at"),

        /**
         * The date and time the repository was last updated.
         * Type: string. Might be null.
         * Format: date-time.
         * Example: "2011-01-26T19:14:43Z".
         */
        UPDATED_AT("updated_at");

        /**
         * The key of the JSON object returned by the GitHub API.
         */
        private final String key;

        /**
         * Constructor.
         * @param json The key of the JSON object returned by the GitHub API.
         */
        KEY(final String json) {
            this.key = json;
        }

        /**
         * Getter for the key.
         * @return The key of the JSON object returned by the GitHub API.
         */
        public String getKey() {
            return this.key;
        }

        /**
         * Extracts the JSON object returned by the GitHub to a map entry.
         * @param object The JSON object returned by the GitHub API.
         * @return The map entry.
         */
        Object value(final JsonObject object) {
            if (!object.containsKey(this.key)) {
                throw new IllegalStateException(
                    String.format(
                        "JSON object '%s' doesn't contain attribute '%s'",
                        object,
                        this.key
                    )
                );
            }
            final JsonValue value = object.get(this.key);
            final Object result;
            final JsonValue.ValueType type = value.getValueType();
            if (type == JsonValue.ValueType.NUMBER) {
                result = ((JsonNumber) value).intValue();
            } else {
                result = ((JsonString) value).getString();
            }
            return result;
        }
    }

    /**
     * Smart RepositoryStatistics.
     *
             * @since 1.8.0
     */
    public static final class Smart {

        /**
         * Repository statistics.
         */
        private final transient RepositoryStatistics stats;

        /**
         * Public ctor.
         * @param repo Repository.
         */
        public Smart(final Repo repo) {
            this(new RepositoryStatistics(repo));
        }

        /**
         * Public ctor.
         * @param statistics Repository statistics.
         */
        public Smart(final RepositoryStatistics statistics) {
            this.stats = statistics;
        }

        /**
         * Number of forks of this repository.
         * @return Number of forks
         * @throws IOException If there is any I/O problem
         */
        public int forks() throws IOException {
            return this.integer(KEY.FORKS_COUNT);
        }

        /**
         * Number of users who have starred this repository.
         * @return Number of stargazers
         * @throws IOException If there is any I/O problem
         */
        public int stargazers() throws IOException {
            return this.integer(KEY.STARGAZERS_COUNT);
        }

        /**
         * Number of users watching the repository.
         * @return Number of watchers
         * @throws IOException If there is any I/O problem
         */
        public int watchers() throws IOException {
            return this.integer(KEY.WATCHERS_COUNT);
        }

        /**
         * The size of the repository.
         * @return Size of the repository
         * @throws IOException If there is any I/O problem
         */
        public int size() throws IOException {
            return this.integer(KEY.SIZE);
        }

        /**
         * The number of open issues in this repository.
         * @return Number of open issues
         * @throws IOException If there is any I/O problem
         */
        public int openIssues() throws IOException {
            return this.integer(KEY.OPEN_ISSUES_COUNT);
        }

        /**
         * The time the repository was created.
         * @return Time the repository was created
         * @throws IOException If there is any I/O problem
         */
        public ZonedDateTime created() throws IOException {
            return this.datetime(KEY.CREATED_AT);
        }

        /**
         * Parses integer from JSON.
         * @param key Json key.
         * @return Integer value.
         * @throws IOException If there is any I/O problem
         */
        private int integer(final KEY key) throws IOException {
            return Integer.parseInt(
                String.valueOf(this.stats.toMap().get(key.getKey()))
            );
        }

        /**
         * Parses datetime from JSON.
         * @param key Json key.
         * @return Datetime value.
         * @throws IOException If there is any I/O problem
         */
        private ZonedDateTime datetime(final KEY key) throws IOException {
            return ZonedDateTime.parse(
                String.valueOf(this.stats.toMap().get(key.getKey()))
            );
        }
    }
}
