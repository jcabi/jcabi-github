/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link PullComment}.
 *
 */
public final class PullCommentTest {

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

    /**
     * PullComment.Smart can fetch the id value from PullComment.
     */
    @Test
    public void fetchesId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).identifier(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the id value of PullComment.
     */
    @Test
    public void updatesId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).identifier(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.ID, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the commit id value from PullComment.
     */
    @Test
    public void fetchesCommitId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.COMMIT_ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).commitId(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the commit id value of PullComment.
     */
    @Test
    public void updatesCommitId() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).commitId(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.COMMIT_ID, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the url value from PullComment.
     */
    @Test
    public void fetchesUrl() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.URL, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).url(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the url value of PullComment.
     */
    @Test
    public void updatesUrl() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).url(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.URL, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the body value from PullComment.
     */
    @Test
    public void fetchesBody() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(PullCommentTest.BODY, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PullComment.Smart(comment).body(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the body value of PullComment.
     */
    @Test
    public void updatesBody() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).body(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(PullCommentTest.BODY, value).build()
        );
    }

    /**
     * PullComment.Smart can retrieve who is the comment author.
     */
    @Test
    public void retrievesAuthor() throws IOException {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
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
