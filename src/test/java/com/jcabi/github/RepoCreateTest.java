/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Repos.RepoCreate}.
 * @since 1.10
 */
final class RepoCreateTest {

    /**
     * RepoCreate.with returns a new instance and leaves the original untouched.
     */
    @Test
    void withDoesNotMutateOriginal() {
        final Repos.RepoCreate original = new Repos.RepoCreate("name", false);
        final JsonValue value = Json.createValue("MIT");
        final Repos.RepoCreate updated = original.with("license_template", value);
        MatcherAssert.assertThat(
            "with(...) must return a different instance",
            updated,
            Matchers.not(Matchers.sameInstance(original))
        );
        final JsonObject before = original.json();
        MatcherAssert.assertThat(
            "original RepoCreate must not carry the new field",
            before.containsKey("license_template"),
            Matchers.is(false)
        );
        final JsonObject after = updated.json();
        MatcherAssert.assertThat(
            "updated RepoCreate must expose the new field in its JSON",
            after.getString("license_template"),
            Matchers.equalTo("MIT")
        );
    }

    /**
     * Successive with() calls preserve every previously added field.
     */
    @Test
    void withAccumulatesAcrossCalls() {
        final Repos.RepoCreate created = new Repos.RepoCreate("name", false)
            .with("has_issues", JsonValue.TRUE)
            .with("has_wiki", JsonValue.FALSE);
        final JsonObject json = created.json();
        MatcherAssert.assertThat(
            "first field must survive a second with() call",
            json.getBoolean("has_issues"),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "second field must appear in the resulting json",
            json.getBoolean("has_wiki"),
            Matchers.is(false)
        );
    }
}
