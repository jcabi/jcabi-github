/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtGitHubITCase {

    @Test
    void authenticatesItself() {
        MatcherAssert.assertThat(
            "Value is null",
            GitHubIT.connect().users().self(),
            Matchers.notNullValue()
        );
    }

    @Test
    void connectsAnonymously() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new Issue.Smart(
                new RtGitHub().repos().get(
                    new Coordinates.Simple("jcabi/jcabi-github")
                ).issues().get(1)
            ).title(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesMeta() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            new RtGitHub().meta().getJsonArray("hooks"),
            Matchers.not(Matchers.empty())
        );
    }

    @Test
    void fetchesEmojis() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub().emojis().getString("+1"),
            Matchers.startsWith("https://")
        );
    }

    @Test
    void authenticatesWithUsernameAndPassword() throws IOException {
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
        MatcherAssert.assertThat(
            "Values are not equal",
            new User.Smart(new RtGitHub(user, password).users().self()).login(),
            Matchers.is(user)
        );
    }

    @Test
    void fetchesUsers() {
        MatcherAssert.assertThat(
            "Iterating over github.users() should return something",
            GitHubIT.connect().users().iterate("").iterator().next(),
            Matchers.anything()
        );
    }
}
