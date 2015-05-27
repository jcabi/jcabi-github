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

import com.google.common.collect.Lists;
import com.jcabi.aspects.Tv;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Language;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Repo}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkRepoTest {

    /**
     * Repo can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void works() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = repos.create(
            new Repos.RepoCreate("test", false)
        );
        MatcherAssert.assertThat(
            repo.coordinates(),
            Matchers.hasToString("jeff/test")
        );
    }

    /**
     * This tests that the milestones() method in MkRepo is working fine.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void returnsMkMilestones() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = repos.create(
            new Repos.RepoCreate("test1", false)
        );
        final Milestones milestones = repo.milestones();
        MatcherAssert.assertThat(milestones, Matchers.notNullValue());
    }

    /**
     * Repo can fetch its commits.
     *
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchCommits() throws IOException {
        final String user = "testuser";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo")
        );
        MatcherAssert.assertThat(repo.commits(), Matchers.notNullValue());
    }

    /**
     * Repo can exponse attributes.
     * @throws Exception If some problem inside
     */
    @Test
    public void exposesAttributes() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        MatcherAssert.assertThat(
            new Repo.Smart(repo).description(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            new Repo.Smart(repo).isPrivate(),
            Matchers.is(false)
        );
    }

    /**
     * Repo can return Stars API.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchStars() throws IOException {
        final String user = "testuser2";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo2")
        );
        MatcherAssert.assertThat(repo.stars(), Matchers.notNullValue());
    }

    /**
     * Repo can return Notifications API.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchNotifications() throws IOException {
        final String user = "testuser3";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo3")
        );
        MatcherAssert.assertThat(repo.notifications(), Matchers.notNullValue());
    }

    /**
     * Repo can return Languages iterable.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchLanguages() throws IOException {
        final String user = "testuser4";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo4")
        );
        final Iterable<Language> languages = repo.languages();
        MatcherAssert.assertThat(languages, Matchers.notNullValue());
        MatcherAssert.assertThat(
            Lists.newArrayList(languages),
            Matchers.hasSize(Tv.THREE)
        );
    }
}
