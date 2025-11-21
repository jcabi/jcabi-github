/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link PullComment}.
 * @since 0.8
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class PullCommentTest {

    /**
     * Id field's name in JSON.
     */
    private static final String ID = "id";

    /**
     * Commit id field's name in JSON.
     */
    private static final String COMMIT_ID = "commit_id";

    /**
     * Url field's name in JSON.
     */
    private static final String URL = "url";

    /**
     * Body field's name in JSON.
     */
    private static final String BODY = "body";

    @Test
    void fetchesId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).identifier(),
            Matchers.is(value)
        );
    }

    @Test
    void updatesId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        new PullComment.Smart(comment).identifier(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.ID, value).build()
        );
    }

    @Test
    void fetchesCommitId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.COMMIT_ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).commitId(),
            Matchers.is(value)
        );
    }

    @Test
    void updatesCommitId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        new PullComment.Smart(comment).commitId(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.COMMIT_ID, value).build()
        );
    }

    @Test
    void fetchesUrl() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.URL, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).url(),
            Matchers.is(value)
        );
    }

    @Test
    void updatesUrl() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        new PullComment.Smart(comment).url(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.URL, value).build()
        );
    }

    @Test
    void fetchesBody() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.BODY, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).body(),
            Matchers.is(value)
        );
    }

    @Test
    void updatesBody() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        new PullComment.Smart(comment).body(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.BODY, value).build()
        );
    }

    @Test
    void retrievesAuthor() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.secure().nextAlphanumeric(10);
        final JsonObject user = Json.createObjectBuilder()
            .add("login", value).build();
        Mockito.doReturn(
            Json.createObjectBuilder().add("user", user).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).author(),
            Matchers.is(value)
        );
    }

}
