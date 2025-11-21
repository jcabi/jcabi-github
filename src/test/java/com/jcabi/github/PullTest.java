/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Pull}.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class PullTest {

    @Test
    void canFetchCommentsCount() throws IOException {
        final int number = 1;
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("comments", number)
                .build()
        ).when(pull).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Pull.Smart(pull).commentsCount(),
            Matchers.is(number)
        );
    }

    @Test
    void getsIssue() {
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
            "Values are not equal",
            new Pull.Smart(pull).issue().number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * Pull.Smart can get pull comments.
     * @throws IOException If some problem inside
     */
    @Test
    void getsPullComments() throws IOException {
        final PullComments comments = Mockito.mock(PullComments.class);
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.when(pull.comments()).thenReturn(comments);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Pull.Smart(pull).comments(),
            Matchers.equalTo(comments)
        );
    }

    /**
     * Pull.Smart can get the author.
     * @throws IOException If some problem inside
     */
    @Test
    void getsAuthor() throws IOException {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.github()).thenReturn(new MkGitHub());
        final Pull pull = Mockito.mock(Pull.class);
        final String login = "rose";
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
            "Values are not equal",
            new Pull.Smart(pull).author().login(),
            Matchers.equalTo(login)
        );
    }
}
