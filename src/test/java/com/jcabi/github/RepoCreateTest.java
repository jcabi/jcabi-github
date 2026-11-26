/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Repository creation settings tests for {@link Repos.RepoCreate}.
 *
 * @since 0.24
 */
final class RepoCreateTest {

    @Test
    void keepsExtraFieldsIndependentAcrossBranches() {
        final Repos.RepoCreate initial = new Repos.RepoCreate("sample", false);
        final String key = "has_wiki";
        final Repos.RepoCreate enabled = initial.with(key, JsonValue.TRUE);
        final Repos.RepoCreate disabled = enabled.with(key, JsonValue.FALSE);
        MatcherAssert.assertThat(initial.json().containsKey(key), Matchers.is(false));
        MatcherAssert.assertThat(enabled.json().getBoolean(key), Matchers.is(true));
        MatcherAssert.assertThat(disabled.json().getBoolean(key), Matchers.is(false));
    }

    @Test
    void keepsExtraFieldsWhenChangingNamedSettings() {
        final String key = "has_issues";
        final Repos.RepoCreate renamed = new Repos.RepoCreate("sample", true)
            .with(key, JsonValue.FALSE)
            .withDescription("renamed project")
            .withName("renamed");
        MatcherAssert.assertThat(renamed.json().get(key), Matchers.equalTo(JsonValue.FALSE));
    }
}
