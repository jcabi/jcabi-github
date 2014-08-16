/**
 * Copyright (c) 2013-2014, jcabi.com
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

import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test case for {@link MkRepos}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkReposTest {

    /**
     * Rule for checking thrown exception.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    /**
     * MkRepos can create a repo.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsRepository() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = MkReposTest.repo(repos, "test", "test repo");
        MatcherAssert.assertThat(
            repo.coordinates(),
            Matchers.hasToString("jeff/test")
        );
    }

    /**
     * MkRepos can create a repo with details.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsRepositoryWithDetails() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = MkReposTest.repo(repos, "hello", "my test repo");
        MatcherAssert.assertThat(
            new Repo.Smart(repo).description(),
            Matchers.startsWith("my test")
        );
    }

    /**
     * MkRepos can remove an existing repo.
     * @throws Exception If some problem inside
     */
    @Test
    public void removesRepo() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = MkReposTest.repo(repos, "remove-me", "This is to be deleted!");
        MatcherAssert.assertThat(
            repos.get(repo.coordinates()),
            Matchers.notNullValue()
        );
        repos.remove(repo.coordinates());
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("repository jeff/remove-me doesn't exist");
        repos.get(repo.coordinates());
    }

    /**
     * MkRepos can iterate repos.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateRepos() throws Exception {
        final String since = "1";
        final Repos repos = new MkRepos(new MkStorage.InFile(), "tom");
        MkReposTest.repo(repos, since, "repo 1");
        MkReposTest.repo(repos, "2", "repo 2");
        MatcherAssert.assertThat(
            repos.iterate(since),
            Matchers.<Repo>iterableWithSize(2)
        );
    }

    /**
     * Create and return Repo to test.
     * @param repos Repos
     * @param name Repo name
     * @param desc Repo description
     * @return Repo
     * @throws Exception if there is any error
     */
    private static Repo repo(final Repos repos, final String name,
        final String desc) throws Exception {
        return repos.create(
            Json.createObjectBuilder()
                .add("name", name)
                .add("description", desc)
                .build()
        );
    }
}
