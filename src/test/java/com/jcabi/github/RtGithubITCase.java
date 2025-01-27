/**
 * Copyright (c) 2013-2025, jcabi.com
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
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Github}.
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtGithubITCase {

    /**
     * RtGithub can authenticate itself.
     */
    @Test
    public void authenticatesItself() {
        final Github github = new GithubIT().connect();
        MatcherAssert.assertThat(
            github.users().self(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can connect anonymously.
     * @throws Exception If some problem inside
     */
    @Test
    public void connectsAnonymously() throws Exception {
        final Github github = new RtGithub();
        MatcherAssert.assertThat(
            new Issue.Smart(
                github.repos().get(
                    new Coordinates.Simple("jcabi/jcabi-github")
                ).issues().get(1)
            ).title(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can fetch meta information.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesMeta() throws Exception {
        final Github github = new RtGithub();
        MatcherAssert.assertThat(
            github.meta().getJsonArray("hooks"),
            Matchers.not(Matchers.empty())
        );
    }

    /**
     * RtGithub can fetch emojis.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesEmojis() throws Exception {
        final Github github = new RtGithub();
        MatcherAssert.assertThat(
            github.emojis().getString("+1"),
            Matchers.startsWith("https://")
        );
    }

    /**
     * RtGithub can authenticate with username and password through HTTP Basic.
     * @throws Exception If some problem inside
     */
    @Test
    public void authenticatesWithUsernameAndPassword() throws Exception {
        final String user = System.getProperty("failsafe.github.user");
        final String password = System.getProperty("failsafe.github.password");
        Assume.assumeThat(
            user,
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
        );
        Assume.assumeThat(
            password,
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
        );
        final Github github = new RtGithub(user, password);
        MatcherAssert.assertThat(
            new User.Smart(github.users().self()).login(),
            Matchers.is(user)
        );
    }

    /**
     * RtGithub can fetch users.
     */
    @Test
    public void fetchesUsers() {
        final Github github = new GithubIT().connect();
        MatcherAssert.assertThat(
            "Iterating over github.users() should return something",
            github.users().iterate("").iterator().next(),
            Matchers.anything()
        );
    }

}
