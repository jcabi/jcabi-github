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
import org.junit.Test;

/**
 * Test case for {@link RtGithub}.
 *
 */
public final class RtGithubTest {

    /**
     * RtGithub can retrieve its repos.
     *
     */
    @Test
    public void retrievesRepos() {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.repos(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve its gists.
     *
     */
    @Test
    public void retrievesGists() {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.gists(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve users.
     *
     */
    @Test
    public void retrievesUsers() {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.users(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve meta information in JSON format.
     *
     */
    @Test
    public void retrievesMetaAsJson() throws IOException {
        final RtGithub github = new RtGithub(
            new FakeRequest().withBody("{\"meta\":\"blah\"}")
        );
        MatcherAssert.assertThat(
            github.meta().getString("meta"),
            Matchers.equalTo("blah")
        );
    }

    /**
     * RtGithub can retrieve emoji information in JSON format.
     *
     */
    @Test
    public void retrievesEmojisAsJson() throws IOException {
        final RtGithub github = new RtGithub(
            new FakeRequest().withBody(
            "{ \"emojikey\": \"urlvalue\" }"
            )
        );
        MatcherAssert.assertThat(
            github.emojis().getString("emojikey"),
            new IsEqual<>("urlvalue")
        );
    }

    /**
     * RtGithub can retrieve the markdown.
     *
     */
    @Test
    public void retrievesMarkdown() {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.markdown(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve the gitignores.
     */
    @Test
    public void retrievesGitignores() {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.gitignores(),
            Matchers.notNullValue()
        );
    }

    /**
     * Github.Time can compare two same Times successfully.
     */
    @Test
    public void testSameTimesAreEqual() {
        final long time = System.currentTimeMillis();
        final Github.Time first = new Github.Time(time);
        final Github.Time second = new Github.Time(time);
        MatcherAssert.assertThat(
            first.equals(second),
            Matchers.is(true)
        );
    }

    /**
     * Github.Time can compare two different Times successfully.
     */
    @Test
    public void testDifferentTimesAreNotEqual() {
        final Github.Time first = new Github.Time(System.currentTimeMillis());
        final Github.Time second = new Github.Time(
            System.currentTimeMillis() + 1
        );
        MatcherAssert.assertThat(
            first.equals(second),
            Matchers.is(false)
        );
    }

    /**
     * RtGithub can compare itself with another object.
     */
    @Test
    public void equalsToAnotherGithub() {
        MatcherAssert.assertThat(
            new RtGithub(new FakeRequest().header("abc", "cde")),
            Matchers.not(
                Matchers.equalTo(
                    new RtGithub(new FakeRequest().header("fgh", "ikl"))
                )
            )
        );
        MatcherAssert.assertThat(
            new RtGithub(new FakeRequest()),
            Matchers.equalTo(new RtGithub(new FakeRequest()))
        );
    }
}
