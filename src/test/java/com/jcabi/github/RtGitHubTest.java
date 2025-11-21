/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
public final class RtGitHubTest {

    @Test
    public void retrievesRepos() {
        final RtGitHub github = new RtGitHub(new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null",
            github.repos(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void retrievesGists() {
        final RtGitHub github = new RtGitHub(new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null",
            github.gists(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void retrievesUsers() {
        final RtGitHub github = new RtGitHub(new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null",
            github.users(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void retrievesMetaAsJson() throws IOException {
        final RtGitHub github = new RtGitHub(
            new FakeRequest().withBody("{\"meta\":\"blah\"}")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            github.meta().getString("meta"),
            Matchers.equalTo("blah")
        );
    }

    @Test
    public void retrievesEmojisAsJson() throws IOException {
        final RtGitHub github = new RtGitHub(
            new FakeRequest().withBody(
            "{ \"emojikey\": \"urlvalue\" }"
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            github.emojis().getString("emojikey"),
            new IsEqual<>("urlvalue")
        );
    }

    @Test
    public void retrievesMarkdown() {
        final RtGitHub github = new RtGitHub(new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null",
            github.markdown(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void retrievesGitignores() {
        final RtGitHub github = new RtGitHub(new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null",
            github.gitignores(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void testSameTimesAreEqual() {
        final long time = System.currentTimeMillis();
        final GitHub.Time first = new GitHub.Time(time);
        final GitHub.Time second = new GitHub.Time(time);
        MatcherAssert.assertThat(
            "Values are not equal",
            first.toString(),
            Matchers.equalTo(second.toString())
        );
    }

    @Test
    public void testDifferentTimesAreNotEqual() {
        final GitHub.Time first = new GitHub.Time(System.currentTimeMillis());
        final GitHub.Time second = new GitHub.Time(
            System.currentTimeMillis() + 1
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            first.equals(second),
            Matchers.is(false)
        );
    }

    @Test
    public void equalsToAnotherGitHub() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub(new FakeRequest().header("abc", "cde")),
            Matchers.not(
                Matchers.equalTo(
                    new RtGitHub(new FakeRequest().header("fgh", "ikl"))
                )
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub(new FakeRequest()),
            Matchers.equalTo(new RtGitHub(new FakeRequest()))
        );
    }
}
