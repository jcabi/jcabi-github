/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkGit}.
 * @since 0.8
 */
final class MkGitTest {

    @Test
    void canFetchOwnRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.git().repo(),
            Matchers.equalTo(repo)
        );
    }

    @Test
    void givesReferences() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().randomRepo().git().references(),
            Matchers.notNullValue()
        );
    }
}
