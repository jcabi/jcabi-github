/**
 * Copyright (c) 2013-2018, jcabi.com
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

import com.jcabi.github.PullRef;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPullRef}.
 *
 * @author Chris Rebert (github@rebertia.com)
 * @version $Id$
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
        final Repo repo = new MkGithub(storage, MkPullRefTest.USERNAME)
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
            pullRef().ref(),
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
            pullRef().sha(),
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
            ((MkBranches) (new MkGithub(storage, MkPullRefTest.USERNAME)
                .randomRepo()
                .branches()
            )).create(MkPullRefTest.REF, MkPullRefTest.SHA)
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
            ((MkBranches) (repo.branches()))
                .create(MkPullRefTest.REF, MkPullRefTest.SHA)
        );
    }
}
