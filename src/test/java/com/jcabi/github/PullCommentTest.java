/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import javax.json.Json;
import javax.json.JsonObject;
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
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).identifier(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the id value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).identifier(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(ID, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the commit id value from PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesCommitId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(COMMIT_ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).commitId(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the commit id value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesCommitId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).commitId(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(COMMIT_ID, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the url value from PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesUrl() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(URL, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).url(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the url value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesUrl() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).url(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(URL, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the body value from PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesBody() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(BODY, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).body(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the body value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesBody() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).body(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(BODY, value).build()
        );
    }

    /**
     * PullComment.Smart can retrieve who is the comment author.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void retrievesAuthor() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final JsonObject user = Json.createObjectBuilder()
            .add("login", value).build();
        Mockito.doReturn(
            Json.createObjectBuilder().add("user", user).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).author(),
            Matchers.is(value)
        );
    }

}
