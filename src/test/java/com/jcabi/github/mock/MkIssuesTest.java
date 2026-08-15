/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkIssues}.
 * @since 0.1
 */
final class MkIssuesTest {

    @Test
    void iteratesIssues() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        repo.issues().create("hey, you", "body of issue");
        repo.issues().create("hey", "body of 2nd issue");
        repo.issues().create("hey again", "body of 3rd issue");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.issues().iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(3)
        );
    }

    @Test
    void createsNewIssueWithCorrectAuthor() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(
                repo.issues().create("hello", "the body")
            ).author().login(),
            Matchers.equalTo(repo.github().users().self().login())
        );
    }

    @Test
    void createsMultipleIssues() throws IOException {
        final Repo repo = new MkGitHub("jeff").repos().create(
            new Repos.RepoCreate("test-3", false)
        );
        for (int idx = 1; idx < 10; ++idx) {
            repo.issues().create("title", "body");
        }
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.issues().iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(9)
        );
    }
}
