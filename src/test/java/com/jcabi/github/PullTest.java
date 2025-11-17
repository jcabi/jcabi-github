/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Pull}.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class PullTest {

    /**
     * Pull.Smart can fetch comments count from Pull.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchCommentsCount() throws Exception {
        final int number = 1;
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("comments", number)
                .build()
        ).when(pull).json();
        MatcherAssert.assertThat(
            new Pull.Smart(pull).commentsCount(),
            Matchers.is(number)
        );
    }

    /**
     * Pull.Smart can get an issue where the pull request is submitted.
     */
    @Test
    public void getsIssue() {
        final int number = 2;
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.number()).thenReturn(number);
        final Issues issues = Mockito.mock(Issues.class);
        Mockito.when(issues.get(2)).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.issues()).thenReturn(issues);
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.when(pull.number()).thenReturn(number);
        Mockito.when(pull.repo()).thenReturn(repo);
        MatcherAssert.assertThat(
            new Pull.Smart(pull).issue().number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * Pull.Smart can get pull comments.
     * @throws IOException If some problem inside
     */
    @Test
    public void getsPullComments() throws IOException {
        final PullComments pullComments = Mockito.mock(PullComments.class);
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.when(pull.comments()).thenReturn(pullComments);
        MatcherAssert.assertThat(
            new Pull.Smart(pull).comments(),
            Matchers.equalTo(pullComments)
        );
    }

    /**
     * Pull.Smart can get the author.
     * @throws IOException If some problem inside
     */
    @Test
    public void getsAuthor() throws IOException {
        final String login = "rose";
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.github()).thenReturn(new MkGithub());
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.when(pull.json()).thenReturn(
            Json.createObjectBuilder()
                .add(
                    "user",
                    Json.createObjectBuilder()
                        .add("login", login)
                        .build()
                )
                .build()
        );
        Mockito.when(pull.repo()).thenReturn(repo);
        MatcherAssert.assertThat(
            new Pull.Smart(pull).author().login(),
            Matchers.equalTo(login)
        );
    }
}
