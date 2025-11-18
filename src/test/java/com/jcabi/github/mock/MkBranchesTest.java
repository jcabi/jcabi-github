/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Branch;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkBranches}.
 */
public final class MkBranchesTest {
    /**
     * MkBranches can create a new branch.
     * @throws IOException if there is any I/O problem
     */
    @Test
    public void createsBranch() throws IOException {
        final String name = "my-new-feature";
        final String sha = "590e188e3d52a8da38cf51d3f9bf598bb46911af";
        final Repo repo = new MkGitHub().randomRepo();
        final Branch branch = ((MkBranches) repo.branches())
            .create(name, sha);
        MatcherAssert.assertThat(
            "Values are not equal",branch.name(), Matchers.equalTo(name));
        MatcherAssert.assertThat(
            "Values are not equal",
            branch.commit().sha(),
            Matchers.equalTo(sha)
        );
        final Coordinates coords = branch.commit().repo().coordinates();
        MatcherAssert.assertThat(
            "Values are not equal",
            coords.user(),
            Matchers.equalTo(repo.coordinates().user())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            coords.repo(),
            Matchers.equalTo(repo.coordinates().repo())
        );
    }

    /**
     * MkBranches can iterate over the repo's branches.
     * @throws IOException if there is any I/O problem
     */
    @Test
    public void iteratesOverBranches() throws IOException {
        final MkBranches branches = (MkBranches) new MkGitHub().randomRepo()
            .branches();
        final String onename = "narf";
        final String onesha = "a86da33b875e8ecbaf75cefcf6d8957cbecb654e";
        branches.create(onename, onesha);
        final String twoname = "zort";
        final String twosha = "ba00fa4fe331c59736b87f52f760e1ccfb293b5f";
        branches.create(twoname, twosha);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            branches.iterate(),
            Matchers.iterableWithSize(2)
        );
        final Iterator<Branch> iter = branches.iterate().iterator();
        final Branch one = iter.next();
        MatcherAssert.assertThat(
            "Values are not equal",one.name(), Matchers.equalTo(onename));
        MatcherAssert.assertThat(
            "Values are not equal",
            one.commit().sha(),
            Matchers.equalTo(onesha)
        );
        final Branch two = iter.next();
        MatcherAssert.assertThat(
            "Values are not equal",two.name(), Matchers.equalTo(twoname));
        MatcherAssert.assertThat(
            "Values are not equal",
            two.commit().sha(),
            Matchers.equalTo(twosha)
        );
    }
}
