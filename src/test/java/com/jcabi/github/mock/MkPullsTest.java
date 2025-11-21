/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Issue;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkPulls}.
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class MkPullsTest {

    /**
     * MkPulls can create a pull.
     * It should create an issue first, and then pull with the same number
     */
    @Test
    public void canCreateAPull() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Pull pull = repo.pulls().create(
            "hello",
            "head-branch",
            "base-branch"
        );
        final Issue.Smart issue = new Issue.Smart(
            repo.issues().get(pull.number())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            issue.title(),
            Matchers.is("hello")
        );
    }

    @Test
    @Disabled
    public void canFetchEmptyListOfPulls() {
        // to be implemented
    }

    @Test
    @Disabled
    public void canFetchSinglePull() {
        // to be implemented
    }
}
