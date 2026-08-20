/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Branch;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkBranches}.
 * @since 0.8
 */
final class MkBranchesTest {

    /**
     * Name of the first branch.
     */
    private static final String FIRST_NAME = "narf";

    /**
     * Commit SHA of the first branch.
     */
    private static final String FIRST_SHA =
        "a86da33b875e8ecbaf75cefcf6d8957cbecb654e";

    /**
     * Name of the second branch.
     */
    private static final String SECOND_NAME = "zort";

    /**
     * Commit SHA of the second branch.
     */
    private static final String SECOND_SHA =
        "ba00fa4fe331c59736b87f52f760e1ccfb293b5f";

    /**
     * MkBranches can name a new branch.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void namesCreatedBranch() throws IOException {
        MatcherAssert.assertThat(
            "Created branch has a wrong name",
            MkBranchesTest.created(new MkGitHub().randomRepo()).name(),
            Matchers.equalTo(MkBranchesTest.FIRST_NAME)
        );
    }

    /**
     * MkBranches can point a new branch to a commit.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void pointsCreatedBranchToCommit() throws IOException {
        MatcherAssert.assertThat(
            "Created branch has a wrong commit",
            MkBranchesTest.created(new MkGitHub().randomRepo())
                .commit().sha(),
            Matchers.equalTo(MkBranchesTest.FIRST_SHA)
        );
    }

    /**
     * MkBranches can put a new branch into the user of its repo.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void putsCreatedBranchIntoUser() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Created branch belongs to a wrong user",
            MkBranchesTest.created(repo).commit().repo()
                .coordinates().user(),
            Matchers.equalTo(repo.coordinates().user())
        );
    }

    /**
     * MkBranches can put a new branch into its repo.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void putsCreatedBranchIntoRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Created branch belongs to a wrong repo",
            MkBranchesTest.created(repo).commit().repo()
                .coordinates().repo(),
            Matchers.equalTo(repo.coordinates().repo())
        );
    }

    /**
     * MkBranches can iterate over the repo's branches.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void iteratesOverBranches() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkBranchesTest.branches().iterate(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * MkBranches can iterate over the name of the first branch.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void iteratesOverNameOfFirstBranch() throws IOException {
        MatcherAssert.assertThat(
            "First branch has a wrong name",
            MkBranchesTest.branches().iterate().iterator().next().name(),
            Matchers.equalTo(MkBranchesTest.FIRST_NAME)
        );
    }

    /**
     * MkBranches can iterate over the commit of the first branch.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void iteratesOverCommitOfFirstBranch() throws IOException {
        MatcherAssert.assertThat(
            "First branch has a wrong commit",
            MkBranchesTest.branches().iterate().iterator().next()
                .commit().sha(),
            Matchers.equalTo(MkBranchesTest.FIRST_SHA)
        );
    }

    /**
     * MkBranches can iterate over the name of the second branch.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void iteratesOverNameOfSecondBranch() throws IOException {
        final Iterator<Branch> iter =
            MkBranchesTest.branches().iterate().iterator();
        iter.next();
        MatcherAssert.assertThat(
            "Second branch has a wrong name",
            iter.next().name(),
            Matchers.equalTo(MkBranchesTest.SECOND_NAME)
        );
    }

    /**
     * MkBranches can iterate over the commit of the second branch.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void iteratesOverCommitOfSecondBranch() throws IOException {
        final Iterator<Branch> iter =
            MkBranchesTest.branches().iterate().iterator();
        iter.next();
        MatcherAssert.assertThat(
            "Second branch has a wrong commit",
            iter.next().commit().sha(),
            Matchers.equalTo(MkBranchesTest.SECOND_SHA)
        );
    }

    private static Branch created(final Repo repo) throws IOException {
        return ((MkBranches) repo.branches()).create(
            MkBranchesTest.FIRST_NAME,
            MkBranchesTest.FIRST_SHA
        );
    }

    private static MkBranches branches() throws IOException {
        final MkBranches branches = (MkBranches) new MkGitHub().randomRepo()
            .branches();
        branches.create(MkBranchesTest.FIRST_NAME, MkBranchesTest.FIRST_SHA);
        branches.create(MkBranchesTest.SECOND_NAME, MkBranchesTest.SECOND_SHA);
        return branches;
    }
}
