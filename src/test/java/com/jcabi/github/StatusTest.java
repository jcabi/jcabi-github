/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.github.mock.MkGitHub;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Status}.
 * @since 0.24
 */
@SuppressWarnings("PMD.TooManyMethods")
final class StatusTest {
    /**
     * Name of state property in Status JSON object.
     */
    private static final String STATE_PROP = "state";

    /**
     * Name of description property in Status JSON object.
     */
    private static final String DESCRIPTION_PROP = "description";

    /**
     * Name of description property in Status JSON object.
     */
    private static final String TARGET_PROP = "target_url";

    /**
     * Status.Smart can fetch its commit.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesCommit() throws Exception {
        final Commit cmmt = StatusTest.commit();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(cmmt, Json.createObjectBuilder().build())
            ).commit(),
            Matchers.equalTo(cmmt)
        );
    }

    /**
     * Status.Smart can fetch its ID number.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesId() throws Exception {
        final int ident = 777;
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().add("id", ident).build()
                )
            ).identifier(),
            Matchers.equalTo(ident)
        );
    }

    /**
     * Status.Smart can fetch its URL.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesUrl() throws Exception {
        final String url = "http://api.jcabi-github.invalid/wherever";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().add("url", url).build()
                )
            ).url(),
            Matchers.equalTo(url)
        );
    }

    /**
     * Status.Smart can fetch its state when it's error.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesErrorState() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .add(StatusTest.STATE_PROP, "error").build()
                )
            ).state(),
            Matchers.equalTo(Status.State.ERROR)
        );
    }

    /**
     * Status.Smart can fetch its state when it's failure.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesFailureState() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .add(StatusTest.STATE_PROP, "failure").build()
                )
            ).state(),
            Matchers.equalTo(Status.State.FAILURE)
        );
    }

    /**
     * Status.Smart can fetch its state when it's pending.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesPendingState() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .add(StatusTest.STATE_PROP, "pending").build()
                )
            ).state(),
            Matchers.equalTo(Status.State.PENDING)
        );
    }

    /**
     * Status.Smart can fetch its state when it's success.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesSuccessState() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .add(StatusTest.STATE_PROP, "success").build()
                )
            ).state(),
            Matchers.equalTo(Status.State.SUCCESS)
        );
    }

    /**
     * Status.Smart can fetch its target URL when it's present.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesPresentTargetUrl() throws Exception {
        final String url = "http://api.jcabi-github.invalid/good-luck";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .add(StatusTest.TARGET_PROP, url).build()
                )
            ).targetUrl(),
            Matchers.equalTo(Optional.of(url))
        );
    }

    /**
     * Status.Smart can fetch its target URL when it's absent.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesAbsentTargetUrl() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().build()
                )
            ).targetUrl(),
            Matchers.equalTo(Optional.<String>absent())
        );
    }

    /**
     * Status.Smart can fetch its target URL when it's null.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesNullTargetUrl() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .addNull(StatusTest.TARGET_PROP).build()
                )
            ).targetUrl(),
            Matchers.equalTo(Optional.<String>absent())
        );
    }

    /**
     * Status.Smart can fetch its description when it's present.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesPresentDescription() throws Exception {
        final String description = "Mostly harmless";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .add(StatusTest.DESCRIPTION_PROP, description).build()
                )
            ).description(),
            Matchers.equalTo(description)
        );
    }

    /**
     * Status.Smart can fetch its description when it's absent.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesAbsentDescription() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().build()
                )
            ).description(),
            Matchers.equalTo("")
        );
    }

    /**
     * Status.Smart can fetch its description when it's null.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesNullDescription() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .addNull(StatusTest.DESCRIPTION_PROP).build()
                )
            ).description(),
            Matchers.equalTo("")
        );
    }

    /**
     * Status.Smart can fetch its context.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesContext() throws Exception {
        final String context = "jcabi/github/tester";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().add("context", context).build()
                )
            ).context(),
            Matchers.equalTo(context)
        );
    }

    /**
     * Status.Smart can fetch its created-at timestamp.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesCreatedAt() throws Exception {
        final String when = "2015-02-27T19:35:32Z";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().add("created_at", when).build()
                )
            ).createdAt(),
            Matchers.equalTo(new GitHub.Time(when).date())
        );
    }

    /**
     * Status.Smart can fetch its last-updated-at timestamp.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesUpdatedAt() throws Exception {
        final String when = "2013-02-27T19:35:32Z";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().add("updated_at", when).build()
                )
            ).updatedAt(),
            Matchers.equalTo(new GitHub.Time(when).date())
        );
    }

    /**
     * Status.Smart can fetch its creator.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesCreator() throws Exception {
        final String login = "bob";
        MatcherAssert.assertThat(
            "Assertion failed",
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder()
                        .add(
                            "creator",
                            Json.createObjectBuilder()
                                .add("login", login).build()
                        ).build()
                )
            ).creator().login(),
            Matchers.equalTo(login)
        );
    }

    /**
     * Returns a test commit to work with.
     * @return Commit
     */
    private static Commit commit() throws IOException {
        return new MkGitHub().randomRepo().git().commits()
            .get("d288364af5028c72e2a2c91c29343bae11fffcbe");
    }
}
