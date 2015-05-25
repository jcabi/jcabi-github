/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.github.Repos;
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
     * Test user to own test repository.
     */
    private static final String REPO_USER = "jacqueline";
    /**
     * Test repository name.
     */
    private static final String REPO_NAME = "wonderful";

    /**
     * MkBranch can fetch its name.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    public void fetchesName() throws IOException {
        final String name = "topic";
        MatcherAssert.assertThat(
            MkBranchTest.branches()
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
            MkBranchTest.branches()
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
        final Coordinates coords = MkBranchTest.branches()
            .create("test", "sha")
            .repo().coordinates();
        MatcherAssert.assertThat(coords.user(), Matchers.equalTo(REPO_USER));
        MatcherAssert.assertThat(coords.repo(), Matchers.equalTo(REPO_NAME));
    }

    /**
     * Mock repo for MkBranch creation.
     * @return The mock repo.
     * @throws IOException If there is any I/O problem
     */
    private static Repo repository() throws IOException {
        return new MkGithub(REPO_USER).repos().create(
            new Repos.RepoCreate(REPO_NAME, false)
        );
    }

    /**
     * MkBranches for MkBranch creation.
     * @return MkBranches
     * @throws IOException If there is any I/O problem
     */
    private static MkBranches branches() throws IOException {
        return (MkBranches) (repository().branches());
    }
}
