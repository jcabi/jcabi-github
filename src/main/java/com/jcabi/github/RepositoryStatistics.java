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

public class RepositoryStatistics {
    private final Repo repo;

    public RepositoryStatistics(final Repo repo) {
        this.repo = repo;
    }

    Map<String, Object> toMap() throws IOException {
        final JsonObject json = repo.json();
        return new MapOf<>(
            Arrays.stream(KEY.values()).map(key -> key.toEntry(json)).collect(Collectors.toList())
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

        KEY(final String key) {
            this.key = key;
        }

        /**
         * String key.
         * @return string key.
         */
        public String getKey() {
            return this.key;
        }

        /**
         * Extracts the JSON object returned by the GitHub to a map entry.
         * @param object The JSON object returned by the GitHub API.
         * @return The map entry.
         */
        private MapEntry<String, Object> toEntry(final JsonObject object) {
            if (!object.containsKey(this.key)) {
                throw new IllegalStateException(
                    String.format("JSON object '%s' doesn't contains mandatory attribute '%s'",
                        object,
                        this.key
                    )
                );
            }
            return new MapEntry<>(this.key, KEY.extract(object.get(this.key)));
        }

        private static Object extract(JsonValue value) {
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

