/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link GitHub}.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtGitHubITCase {

    @Test
    public void authenticatesItself() {
        final GitHub github = GitHubIT.connect();
        MatcherAssert.assertThat(
            "Value is null",
            github.users().self(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void connectsAnonymously() throws IOException {
        final GitHub github = new RtGitHub();
        MatcherAssert.assertThat(
            "Value is null",
            new Issue.Smart(
                github.repos().get(
                    new Coordinates.Simple("jcabi/jcabi-github")
                ).issues().get(1)
            ).title(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void fetchesMeta() throws IOException {
        final GitHub github = new RtGitHub();
        MatcherAssert.assertThat(
            "Collection is not empty",
            github.meta().getJsonArray("hooks"),
            Matchers.not(Matchers.empty())
        );
    }

    @Test
    public void fetchesEmojis() throws IOException {
        final GitHub github = new RtGitHub();
        MatcherAssert.assertThat(
            "Values are not equal",
            github.emojis().getString("+1"),
            Matchers.startsWith("https://")
        );
    }

    @Test
    public void authenticatesWithUsernameAndPassword() throws IOException {
        final String user = System.getProperty("failsafe.github.user");
        final String password = System.getProperty("failsafe.github.password");
        Assumptions.assumeTrue(
            user != null && !user.isBlank(),
            "GitHub user is required for this test"
        );
        Assumptions.assumeTrue(
            password != null && !password.isBlank(),
            "GitHub password is required for this test"
        );
        final GitHub github = new RtGitHub(user, password);
        MatcherAssert.assertThat(
            "Values are not equal",
            new User.Smart(github.users().self()).login(),
            Matchers.is(user)
        );
    }

    @Test
    public void fetchesUsers() {
        final GitHub github = GitHubIT.connect();
        MatcherAssert.assertThat(
            "Iterating over github.users() should return something",
            github.users().iterate("").iterator().next(),
            Matchers.anything()
        );
    }

}
