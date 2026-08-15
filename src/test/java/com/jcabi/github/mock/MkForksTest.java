/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkForks}.
 * @since 0.8
 */
final class MkForksTest {

    @Test
    @Disabled
    void createsFork() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkForks(
                new MkStorage.InFile(),
                "Test", new Coordinates.Simple("tests", "forks")
            ).create("blah"),
            Matchers.notNullValue()
        );
    }

    @Test
    void iteratesForks() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        repo.forks().create("Organization");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.forks().iterate("Order"),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void iteratesCreatedFork() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            repo.forks().iterate("Order"),
            Matchers.hasItem(repo.forks().create("Organization"))
        );
    }
}
