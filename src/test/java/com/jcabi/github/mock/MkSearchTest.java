/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Search;
import java.io.IOException;
import java.util.EnumMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkSearch}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkSearchTest {

    @Test
    public void canSearchForRepos() throws IOException {
        final MkGitHub github = new MkGitHub();
        github.repos().create(
            new Repos.RepoCreate("TestRepo", false)
        );
        MatcherAssert.assertThat(
            "Collection is not empty",
            github.search().repos("TestRepo", "updated", Search.Order.ASC),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @Test
    public void canSearchForIssues() throws IOException {
        final MkGitHub github = new MkGitHub();
        final Repo repo = github.repos().create(
            new Repos.RepoCreate("TestIssues", false)
        );
        repo.issues().create("test issue", "TheTest");
        MatcherAssert.assertThat(
            "Collection is not empty",
            github.search().issues(
                "TheTest",
                "updated",
                Search.Order.DESC,
                new EnumMap<>(Search.Qualifier.class)
            ),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @Test
    public void canSearchForUsers() throws IOException {
        final MkGitHub github = new MkGitHub("jeff");
        github.users().self();
        MatcherAssert.assertThat(
            "Collection is not empty",
            github.search().users("jeff", "repositories", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @Test
    public void canSearchForCodes() throws IOException {
        final MkGitHub github = new MkGitHub("jeff");
        github.repos().create(
            new Repos.RepoCreate("TestCode", false)
        );
        MatcherAssert.assertThat(
            "Collection is not empty",
            github.search().codes("jeff", "repositories", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
