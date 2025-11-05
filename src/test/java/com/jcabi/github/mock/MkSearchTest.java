/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Search;
import java.util.EnumMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkSearch}.
 *
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkSearchTest {

    /**
     * MkSearch can search for repos.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForRepos() throws Exception {
        final MkGithub github = new MkGithub();
        github.repos().create(
            new Repos.RepoCreate("TestRepo", false)
        );
        MatcherAssert.assertThat(
            github.search().repos("TestRepo", "updated", Search.Order.ASC),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkSearch can search for issues.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForIssues() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.repos().create(
            new Repos.RepoCreate("TestIssues", false)
        );
        repo.issues().create("test issue", "TheTest");
        MatcherAssert.assertThat(
            github.search().issues(
                "TheTest",
                "updated",
                Search.Order.DESC,
                new EnumMap<>(Search.Qualifier.class)
            ),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkSearch can search for users.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForUsers() throws Exception {
        final MkGithub github = new MkGithub("jeff");
        github.users().self();
        MatcherAssert.assertThat(
            github.search().users("jeff", "repositories", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkSearch can search for codes.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForCodes() throws Exception {
        final MkGithub github = new MkGithub("jeff");
        github.repos().create(
            new Repos.RepoCreate("TestCode", false)
        );
        MatcherAssert.assertThat(
            github.search().codes("jeff", "repositories", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
