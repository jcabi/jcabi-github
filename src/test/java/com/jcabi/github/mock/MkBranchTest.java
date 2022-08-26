/**
 * Copyright (c) 2013-2022, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkBranch}.
 *
 * @author Chris Rebert (github@rebertia.com)
 * @version $Id$
 */
public final class MkBranchTest {
    /**
     * MkBranch can fetch its name.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    public void fetchesName() throws IOException {
        final String name = "topic";
        MatcherAssert.assertThat(
            MkBranchTest.branches(new MkGithub().randomRepo())
                .create(name, "f8dfc75138a2b57859b65cfc45239978081b8de4")
                .name(),
            Matchers.equalTo(name)
        );
    }

    /**
     * MkBranch can fetch its commit.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    public void fetchesCommit() throws IOException {
        final String sha = "ad1298cac285d601cd66b37ec8989836d7c6e651";
        MatcherAssert.assertThat(
            MkBranchTest.branches(new MkGithub().randomRepo())
                .create("feature-branch", sha).commit().sha(),
            Matchers.equalTo(sha)
        );
    }

    /**
     * MkBranch can fetch its repo.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    public void fetchesRepo() throws IOException {
        final Repo repo = new MkGithub().randomRepo();
        final Coordinates coords = MkBranchTest.branches(repo)
            .create("test", "sha")
            .repo().coordinates();
        MatcherAssert.assertThat(
            coords.user(),
            Matchers.equalTo(repo.coordinates().user())
        );
        MatcherAssert.assertThat(
            coords.repo(),
            Matchers.equalTo(repo.coordinates().repo())
        );
    }

    /**
     * MkBranches for MkBranch creation.
     * @param repo Repository to get MkBranches of
     * @return MkBranches
     */
    private static MkBranches branches(final Repo repo) {
        return (MkBranches) (repo.branches());
    }
}
