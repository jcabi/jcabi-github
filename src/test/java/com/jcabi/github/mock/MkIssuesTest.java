/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Tv;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.immutable.ArrayMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkIssues}.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkIssuesTest {

    /**
     * MkIssues can list issues.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesIssues() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.issues().create("hey, you", "body of issue");
        repo.issues().create("hey", "body of 2nd issue");
        repo.issues().create("hey again", "body of 3rd issue");
        MatcherAssert.assertThat(
            repo.issues().iterate(new ArrayMap<>()),
            Matchers.<Issue>iterableWithSize(Tv.THREE)
        );
    }

    /**
     * MkIssues can create a new issue with correct author.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsNewIssueWithCorrectAuthor() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue.Smart issue = new Issue.Smart(
            repo.issues().create("hello", "the body")
        );
        MatcherAssert.assertThat(
            issue.author().login(),
            Matchers.equalTo(repo.github().users().self().login())
        );
    }

    /**
     * MkIssues can create a multiple issues.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsMultipleIssues() throws Exception {
        final Github github = new MkGithub("jeff");
        final Repo repo = github.repos().create(
            new Repos.RepoCreate("test-3", false)
        );
        for (int idx = 1; idx < Tv.TEN; ++idx) {
            repo.issues().create("title", "body");
        }
    }
}
