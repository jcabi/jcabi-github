/**
 * Copyright (c) 2013-2014, JCabi.com
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
 * Test case for {@link MkRepoCommit).
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
public final class MkRepoCommitTest {

    /**
     * MkRepoCommit can return repository.
     * @throws IOException If some problem inside
     */
    @Test
    public void getRepo() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final Repo repo = this.repo(storage);
        MatcherAssert.assertThat(
            new MkRepoCommit(
                storage, repo, "6dcb09b5b57875f334f61aebed695e2e4193db5e"
            ).repo(), Matchers.equalTo(repo)
        );
    }

    /**
     * MkRepoCommit can return sha.
     * @throws IOException If some problem inside
     */
    @Test
    public void getSha() throws IOException {
        final String sha = "51cabb8e759852a6a40a7a2a76ef0afd4beef96d";
        final MkStorage storage = new MkStorage.InFile();
        MatcherAssert.assertThat(
            new MkRepoCommit(storage, this.repo(storage), sha).sha(),
            Matchers.equalTo(sha)
        );
    }

    /**
     * Create repository for test.
     * @param storage The storage
     * @return Repo
     * @throws IOException If some problem inside
     */
    private Repo repo(final MkStorage storage) throws IOException {
        final String login = "test_login";
        return new MkRepo(
            storage,
            login,
            new Coordinates.Simple(login, "test_repo")
        );
    }
}
