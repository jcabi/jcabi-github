package com.jcabi.github;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.JsonObject;
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

    private enum KEY {
        LANGUAGE("language"),
        FORKS_COUNT("forks_count"),
        STARGAZERS_COUNT("stargazers_count"),
        WATCHERS_COUNT("watchers_count"),
        SIZE("size"),
        OPEN_ISSUES_COUNT("open_issues_count"),
        CREATED_AT("created_at"),
        UPDATED_AT("updated_at");

        private final String key;

        KEY(final String key) {
            this.key = key;
        }

        private MapEntry<String, Object> toEntry(final JsonObject object) {
            final String k = this.getKey();
            final JsonValue value = object.get(k);
            return new MapEntry<>(k, value.toString());
        }

        public String getKey() {
            return key;
        }
    }
}

