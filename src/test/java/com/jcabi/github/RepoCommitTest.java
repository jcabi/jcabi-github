/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RepoCommit}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RepoCommitTest {

    /**
     * RepoCommit.Smart can fetch url property from RepoCommit.
     */
    @Test
    public final void fetchesUrl() throws IOException, MalformedURLException, URISyntaxException {
        final RepoCommit commit = Mockito.mock(RepoCommit.class);
        // @checkstyle LineLength (1 line)
        final String prop = "https://api.github.com/repos/pengwynn/octokit/contents/README.md";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", prop)
                .build()
        ).when(commit).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RepoCommit.Smart(commit).url(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    /**
     * RepoCommit.Smart can fetch message property from RepoCommit.
     */
    @Test
    public final void fetchesMessage() throws IOException {
        final RepoCommit commit = Mockito.mock(RepoCommit.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add(
                "commit",
                Json.createObjectBuilder().add("message", "hello, world!")
            ).build()
        ).when(commit).json();
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new RepoCommit.Smart(commit).message(),
            Matchers.startsWith("hello, ")
        );
    }

    /**
     * RtRepoCommit can verify status.
     * @throws IOException If fails
     */
    @Test
    public final void verifiesStatus() throws IOException {
        final RepoCommit commit = Mockito.mock(RepoCommit.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add(
                "commit",
                Json.createObjectBuilder().add(
                    "verification",
                    Json.createObjectBuilder().add("verified", true)
                ).build()
            ).build()
        ).when(commit).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RepoCommit.Smart(commit).isVerified(),
            Matchers.is(true)
        );
    }

    /**
     * RtRepoCommit can read author's login.
     * @throws IOException If fails
     */
    @Test
    public final void readsAuthorLogin() throws IOException {
        final RepoCommit commit = Mockito.mock(RepoCommit.class);
        final String login = "jeff";
        Mockito.doReturn(
            Json.createObjectBuilder().add(
                "commit",
                Json.createObjectBuilder().add(
                    "author",
                    Json.createObjectBuilder().add("name", login)
                ).build()
            ).build()
        ).when(commit).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RepoCommit.Smart(commit).author(),
            Matchers.equalTo(login)
        );
    }
}
