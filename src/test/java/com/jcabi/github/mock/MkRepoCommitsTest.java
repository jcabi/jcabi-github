/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkRepoCommits}.
 */
public final class MkRepoCommitsTest {

    /**
     * MkRepoCommits can return commits' iterator.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnIterator() throws IOException {
        final String user =  "testuser1";
        MatcherAssert.assertThat(
            "Value is null",
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo1")
            ).iterate(Collections.emptyMap()),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepoCommits can get a commit.
     * @throws IOException if some problem inside
     */
    @Test
    public void getCommit() throws IOException {
        final String user =  "testuser2";
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db5e";
        MatcherAssert.assertThat(
            "Value is null",
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo2")
            ).get(sha),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepoCommits can compare commits.
     * @throws IOException if some problem inside
     */
    @Test
    public void canCompare() throws IOException {
        final String user =  "testuser3";
        MatcherAssert.assertThat(
            "Value is null",
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo3")
            ).compare("5339b8e35b", "9b2e6efde9"),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepoCommits can compare commits as diff fromat.
     */
    @Test
    public void canCompareAsDiffFormat() throws IOException {
        final String user =  "testuser4";
        final String base =  "c034abc";
        final String head =  "a0ed832";
        MatcherAssert.assertThat(
            "Assertion failed",
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo4")
            ).diff(base, head),
            Matchers.stringContainsInOrder(Arrays.asList(base, head))
        );
    }

    /**
     * MkRepoCommits can compare commits as patch.
     */
    @Test
    public void canCompareAsPatch() throws IOException {
        final String user =  "testuser5";
        final String head = "9b2e6e7de9";
        MatcherAssert.assertThat(
            "Assertion failed",
            new MkRepoCommits(
                new MkStorage.InFile(), user,
                new Coordinates.Simple(user, "testrepo5")
            ).patch("5c39b8e35b", head),
            Matchers.stringContainsInOrder(
                Arrays.asList(
                    head, "From:", "Date:", "Subject:", "files changed",
                    "insertions", "deletions"
                )
            )
        );
    }
}
