/**
 * Copyright (c) 2013-2023, jcabi.com
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
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;

/**
 * Repository statistics.
 *
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @version $Id $
 * @since 1.8.0
 * @todo #1660:90min Add RepositoryStatistics.Smart.
 *  Implement RepositoryStatistics.Smart and use it to retrieve repository
 *  statistics with the following methods:
 *  - RepositoryStatistics.Smart#language()
 *  - RepositoryStatistics.Smart#forksCount()
 *  - RepositoryStatistics.Smart#stargazers()
 *  - RepositoryStatistics.Smart#watchers()
 *  - RepositoryStatistics.Smart#size()
 *  - RepositoryStatistics.Smart#openIssues()
 *  In other words, it would be convenient to have particular methods with
 *  understandable names instead of using toMap() method.
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
        return new MapOf<>(
            Arrays.stream(KEY.values())
                .map(key -> key.toEntry(json))
                .collect(Collectors.toList())
        );
    }

    /**
     * Keys of the JSON object returned by the GitHub API.
     *
     * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
     * @version $Id $
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
         * Extracts the JSON object returned by the GitHub to a map entry.
         * @param object The JSON object returned by the GitHub API.
         * @return The map entry.
         */
        MapEntry<String, Object> toEntry(final JsonObject object) {
            if (!object.containsKey(this.key)) {
                throw new IllegalStateException(
                    String.format(
                        "JSON object '%s' doesn't contain attribute '%s'",
                        object,
                        this.key
                    )
                );
            }
            return new MapEntry<>(
                this.key,
                KEY.extract(object.get(this.key))
            );
        }

        /**
         * Extracts the JSON value to a Java object.
         * @param value JSON value.
         * @return Java object.
         */
        private static Object extract(final JsonValue value) {
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
}

