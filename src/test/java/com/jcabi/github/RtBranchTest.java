/**
 * Copyright (c) 2013-2020, jcabi.com
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
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtBranch}.
 *
 * @author Chris Rebert (github@rebertia.com)
 * @version $Id$
 */
final class RtBranchTest {
    /**
     * Test branch name.
     */
    private static final String BRANCH_NAME = "topic";
    /**
     * Commit SHA for test branch.
     * @checkstyle LineLengthCheck (2 lines)
     */
    private static final String SHA = "b9b0b8a357bbf70f7c9f8ef17160ee31feb508a9";

    /**
     * RtBranch can fetch its commit.
     * @throws Exception if a problem occurs.
     */
    @Test
    void fetchesCommit() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Commit commit = RtBranchTest.newBranch(repo).commit();
        MatcherAssert.assertThat(commit.sha(), Matchers.equalTo(SHA));
        final Coordinates coords = commit.repo().coordinates();
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
     * RtBranch can fetch its branch name.
     * @throws Exception if a problem occurs.
     */
    @Test
    void fetchesName() throws Exception {
        MatcherAssert.assertThat(
            RtBranchTest.newBranch(new MkGithub().randomRepo()).name(),
            Matchers.equalTo(BRANCH_NAME)
        );
    }

    /**
     * RtBranch can fetch its repo.
     * @throws Exception if a problem occurs.
     */
    @Test
    void fetchesRepo() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Coordinates coords = RtBranchTest.newBranch(repo)
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
     * RtBranch for testing.
     * @param repo Repository to create the branch in
     * @return The RtBranch.
     * @throws IOException If there is any I/O problem
     */
    private static Branch newBranch(final Repo repo) throws IOException {
        return new RtBranch(
            new FakeRequest(),
            repo,
            BRANCH_NAME,
            SHA
        );
    }
}
