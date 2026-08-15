/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtGitHub}.
 * @since 0.1
 */
final class RtGitHubTest {

    @Test
    void retrievesRepos() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtGitHub(new FakeRequest()).repos(),
            Matchers.notNullValue()
        );
    }

    @Test
    void retrievesGists() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtGitHub(new FakeRequest()).gists(),
            Matchers.notNullValue()
        );
    }

    @Test
    void retrievesUsers() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtGitHub(new FakeRequest()).users(),
            Matchers.notNullValue()
        );
    }

    @Test
    void retrievesMetaAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub(
                new FakeRequest().withBody("{\"meta\":\"blah\"}")
            ).meta().getString("meta"),
            Matchers.equalTo("blah")
        );
    }

    @Test
    void retrievesEmojisAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub(
                new FakeRequest().withBody(
                "{ \"emojikey\": \"urlvalue\" }"
                )
            ).emojis().getString("emojikey"),
            new IsEqual<>("urlvalue")
        );
    }

    @Test
    void retrievesMarkdown() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtGitHub(new FakeRequest()).markdown(),
            Matchers.notNullValue()
        );
    }

    @Test
    void retrievesGitignores() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtGitHub(new FakeRequest()).gitignores(),
            Matchers.notNullValue()
        );
    }

    @Test
    void equalsSameTimes() {
        final long time = System.currentTimeMillis();
        MatcherAssert.assertThat(
            "Values are not equal",
            new GitHub.Time(time).toString(),
            Matchers.equalTo(new GitHub.Time(time).toString())
        );
    }

    @Test
    void differsFromOtherTimes() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new GitHub.Time(System.currentTimeMillis()).equals(
                new GitHub.Time(
                    System.currentTimeMillis() + 1
                    )
            ),
            Matchers.is(false)
        );
    }

    @Test
    void differsFromGitHubWithOtherHeaders() {
        MatcherAssert.assertThat(
            "GitHubs with different headers are the same",
            new RtGitHub(new FakeRequest().header("abc", "cde")),
            Matchers.not(
                Matchers.equalTo(
                    new RtGitHub(new FakeRequest().header("fgh", "ikl"))
                )
            )
        );
    }

    @Test
    void equalsToAnotherGitHub() {
        MatcherAssert.assertThat(
            "GitHubs with the same request are different",
            new RtGitHub(new FakeRequest()),
            Matchers.equalTo(new RtGitHub(new FakeRequest()))
        );
    }
}
