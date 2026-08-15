/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkPulls}.
 * @since 1.0
 */
final class MkPullsTest {

    /**
     * MkPulls can create a pull.
     * It should create an issue first, and then pull with the same number
     */
    @Test
    void canCreateAPull() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(
                repo.issues().get(
                    repo.pulls().create(
                        "hello",
                        "head-branch",
                        "base-branch"
                        ).number()
                )
            ).title(),
            Matchers.is("hello")
        );
    }

    @Test
    @Disabled
    void canFetchEmptyListOfPulls() {
        Assertions.fail("Fetching of an empty list of pulls is not tested yet");
    }

    @Test
    @Disabled
    void canFetchSinglePull() {
        Assertions.fail("Fetching of a single pull is not tested yet");
    }
}
