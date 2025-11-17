/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PullRef;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPullRef}.
 *
 * @since 0.24
 */
public final class MkPullRefTest {
    /**
     * Test ref.
     */
    private static final String REF = "awesome-branch";
    /**
     * Test commit SHA.
     */
    private static final String SHA =
        "361bb23ed4c028914d45d53c3727c48b035ee643";
    /**
     * Test username.
     */
    private static final String USERNAME = "myrtle";

    /**
     * MkPullRef can fetch its repo.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void fetchesRepo() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final Repo repo = new MkGitHub(storage, MkPullRefTest.USERNAME)
            .randomRepo();
        MatcherAssert.assertThat(
            MkPullRefTest.pullRef(storage, repo).repo().coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    /**
     * MkPullRef can fetch its ref name.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void fetchesRef() throws IOException {
        MatcherAssert.assertThat(
            MkPullRefTest.pullRef().ref(),
            Matchers.equalTo(MkPullRefTest.REF)
        );
    }

    /**
     * MkPullRef can fetch its commit sha.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void fetchesSha() throws IOException {
        MatcherAssert.assertThat(
            MkPullRefTest.pullRef().sha(),
            Matchers.equalTo(MkPullRefTest.SHA)
        );
    }

    /**
     * Returns an MkPullRef for testing.
     * @return MkPullRef for testing
     * @throws IOException If there is an I/O problem
     */
    private static PullRef pullRef() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        return new MkPullRef(
            storage,
            ((MkBranches) new MkGitHub(storage, MkPullRefTest.USERNAME)
                .randomRepo()
                .branches()).create(MkPullRefTest.REF, MkPullRefTest.SHA)
        );
    }

    /**
     * Returns a pull request ref for testing.
     * @param storage Storage
     * @param repo Repo to create the pull request ref in
     * @return PullRef for testing
     * @throws IOException If there is an I/O problem
     */
    private static PullRef pullRef(
        final MkStorage storage,
        final Repo repo
    ) throws IOException {
        return new MkPullRef(
            storage,
            ((MkBranches) repo.branches())
                .create(MkPullRefTest.REF, MkPullRefTest.SHA)
        );
    }
}
