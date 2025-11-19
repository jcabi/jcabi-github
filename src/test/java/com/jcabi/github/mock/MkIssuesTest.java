/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Tv;
import com.jcabi.github.GitHub;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkIssues}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkIssuesTest {

    @Test
    public void iteratesIssues() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        repo.issues().create("hey, you", "body of issue");
        repo.issues().create("hey", "body of 2nd issue");
        repo.issues().create("hey again", "body of 3rd issue");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.issues().iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(Tv.THREE)
        );
    }

    @Test
    public void createsNewIssueWithCorrectAuthor() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Issue.Smart issue = new Issue.Smart(
            repo.issues().create("hello", "the body")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            issue.author().login(),
            Matchers.equalTo(repo.github().users().self().login())
        );
    }

    @Test
    public void createsMultipleIssues() throws IOException {
        final GitHub github = new MkGitHub("jeff");
        final Repo repo = github.repos().create(
            new Repos.RepoCreate("test-3", false)
        );
        for (int idx = 1; idx < Tv.TEN; ++idx) {
            repo.issues().create("title", "body");
        }
    }
}
