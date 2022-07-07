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
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration case for {@link RtRepos}.
 *
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
@OAuthScope({ Scope.REPO, Scope.DELETE_REPO })
public class RtReposITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    /**
     * RtRepos create repository test.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public final void create() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(repo, Matchers.notNullValue());
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtRepos should fail on creation of two repos with the same name.
     * @throws Exception If some problem inside
     */
    @Test(expected = AssertionError.class)
    public final void failsOnCreationOfTwoRepos() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            repos.create(
                new Repos.RepoCreate(repo.coordinates().repo(), false)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtRepos exists repository test.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public final void exists() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                repos.exists(repo.coordinates()),
                Matchers.equalTo(true)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtRepos create repository test.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public final void createWithOrganization() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = repos.create(
            new Repos.RepoCreate("test", false).withOrganization("myorg")
        );
        try {
            MatcherAssert.assertThat(
                repo.coordinates(),
                Matchers.hasToString("/orgs/myorg/repos/test")
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

}
