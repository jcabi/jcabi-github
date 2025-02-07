/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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

import com.jcabi.github.CommitsComparison;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RepoCommit;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkCommitsComparison).
 */
public final class MkCommitsComparisonTest {

    /**
     * MkCommitsComparison can get a repo.
     * @throws IOException if some problem inside
     */
    @Test
    public void getRepo() throws IOException {
        final String user = "test_user";
        MatcherAssert.assertThat(
            new MkCommitsComparison(
                new MkStorage.InFile(), user,
                new Coordinates.Simple(user, "test_repo")
            ).repo(), Matchers.notNullValue()
        );
    }
    /**
     * MkCommitsComparison can get a JSON.
     * @throws Exception if some problem inside
     */
    @Test
    public void canGetJson() throws Exception {
        MatcherAssert.assertThat(
            new MkCommitsComparison(
                new MkStorage.InFile(), "test1", new Coordinates.Simple(
                    "test_user1", "test_repo1"
                )
            ).json().getString("status"),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            new MkCommitsComparison(
                new MkStorage.InFile(), "test2", new Coordinates.Simple(
                    "test_user2", "test_repo2"
                )
            ).json().getInt("ahead_by"),
            Matchers.notNullValue()
        );
    }

    /**
     * MkCommitsComparison can get a JSON with commits.
     * @throws Exception if some problem inside
     */
    @Test
    public void canGetJsonWithCommits() throws Exception {
        final CommitsComparison cmp = new MkCommitsComparison(
            new MkStorage.InFile(), "test-9",
            new Coordinates.Simple("test_user_A", "test_repo_B")
        );
        MatcherAssert.assertThat(
            new CommitsComparison.Smart(cmp).commits(),
            Matchers.<RepoCommit>iterableWithSize(0)
        );
        MatcherAssert.assertThat(
            cmp.json().getJsonArray("commits"),
            Matchers.notNullValue()
        );
    }
}
