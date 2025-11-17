/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import java.io.IOException;
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
     */
    @Test
    public void connectsAnonymously() throws IOException {
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
     */
    @Test
    public void fetchesMeta() throws IOException {
        final Github github = new RtGithub();
        MatcherAssert.assertThat(
            github.meta().getJsonArray("hooks"),
            Matchers.not(Matchers.empty())
        );
    }

    /**
     * RtGithub can fetch emojis.
     */
    @Test
    public void fetchesEmojis() throws IOException {
        final Github github = new RtGithub();
        MatcherAssert.assertThat(
            github.emojis().getString("+1"),
            Matchers.startsWith("https://")
        );
    }

    /**
     * RtGithub can authenticate with username and password through HTTP Basic.
     */
    @Test
    public void authenticatesWithUsernameAndPassword() throws IOException {
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
