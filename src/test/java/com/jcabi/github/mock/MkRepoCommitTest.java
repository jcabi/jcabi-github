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

import com.google.common.io.Files;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.File;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link MkRepoCommit).
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
public final class MkRepoCommitTest {

    /**
     * The fist test key.
     */
    static final String SHA1 = "6dcb09b5b57875f334f61aebed695e2e4193db5e";
    /**
     * The second test key.
     */
    static final String SHA2 = "51cabb8e759852a6a40a7a2a76ef0afd4beef96d";

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
                storage, repo, SHA1
            ).repo(), Matchers.equalTo(repo)
        );
    }

    /**
     * MkRepoCommit can return sha.
     * @throws IOException If some problem inside
     */
    @Test
    public void getSha() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MatcherAssert.assertThat(
            new MkRepoCommit(storage, this.repo(storage), SHA2).sha(),
            Matchers.equalTo(SHA2)
        );
    }

    /**
     * MkRepoCommit should be able to compare different instances.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    @Ignore
    public void canCompareInstances() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        final Repo repoa = new MkRepo(
            storage, "login1",
            new Coordinates.Simple("test_login1", "test_repo1")
        );
        final Repo repob = new MkRepo(
            storage, "login2",
            new Coordinates.Simple("test_login2", "test_repo2")
        );
        final MkRepoCommit less =  new MkRepoCommit(
            storage, repoa, SHA1
        );
        final MkRepoCommit greater =  new MkRepoCommit(
            storage, repob, SHA2
        );
        MatcherAssert.assertThat(
            less.compareTo(greater),
            Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less),
            Matchers.greaterThan(0)
        );
    }
    /**
     * MkRepoCommit can get a JSON.
     * @throws Exception if some problem inside
     */
    @Test
    @Ignore
    public void canGetJson() throws Exception {
        final File file = File.createTempFile("jcabi-github", ".xml");
        final MkStorage storage = new MkStorage.InFile(file);
        final StringBuffer contentBuffer = new StringBuffer();
        contentBuffer.append("<github><repos>");
        contentBuffer.append("<repo coords='test_login/test_repo'>");
        contentBuffer.append("<commits><commit sha='");
        contentBuffer.append(SHA1);
        contentBuffer.append("'>");
        contentBuffer.append("Hello world</commit></commits></repo>");
        contentBuffer.append("</repos></github>");
        Files.write(contentBuffer.toString().getBytes(), file);
        final MkRepoCommit repoCommit = new MkRepoCommit(
            storage, this.repo(storage), SHA1);
        MatcherAssert.assertThat(
            repoCommit.json(), Matchers.notNullValue()
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
