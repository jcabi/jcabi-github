/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Statuses}.
 * @since 0.24
 */
public final class StatusesTest {
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
     * Name of context property in Status JSON object.
     */

    private static final String CONTEXT_PROP = "context";
    /**
     * Test status URL.
     */

    private static final String URL = "http://status.jcabi-github.invalid/42";
    /**
     * Test commit status context string.
     */

    private static final String CONTEXT = "jcabi/github/test";

    /**
     * StatusCreate can convert itself to JSON.
     */
    @Test
    public void convertsToJsonWhenAllPresent() {
        final String success = "Everything is not so awesome";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Statuses.StatusCreate(Status.State.ERROR)
                .withTargetUrl(Optional.of(StatusesTest.URL))
                .withDescription(success)
                .withContext(Optional.of(StatusesTest.CONTEXT))
                .json().toString(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(StatusesTest.STATE_PROP, "error")
                    .add(StatusesTest.DESCRIPTION_PROP, success)
                    .add(StatusesTest.CONTEXT_PROP, StatusesTest.CONTEXT)
                    .add(StatusesTest.TARGET_PROP, StatusesTest.URL)
                    .build().toString()
            )
        );
    }

    /**
     * StatusCreate can convert itself to JSON when it has no URL.
     */
    @Test
    public void convertsToJsonWhenUrlAbsent() {
        final String success = "Living the dream!";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Statuses.StatusCreate(Status.State.SUCCESS)
                .withDescription(success)
                .withContext(Optional.of(StatusesTest.CONTEXT))
                .json().toString(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(StatusesTest.STATE_PROP, "success")
                    .add(StatusesTest.DESCRIPTION_PROP, success)
                    .add(StatusesTest.CONTEXT_PROP, StatusesTest.CONTEXT)
                    .build().toString()
            )
        );
    }

    /**
     * StatusCreate can convert itself to JSON when it has no description.
     */
    @Test
    public void convertsToJsonWhenDescriptionAbsent() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Statuses.StatusCreate(Status.State.FAILURE)
                .withTargetUrl(Optional.of(StatusesTest.URL))
                .withContext(Optional.of(StatusesTest.CONTEXT))
                .json().toString(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(StatusesTest.STATE_PROP, "failure")
                    .add(StatusesTest.DESCRIPTION_PROP, "")
                    .add(StatusesTest.CONTEXT_PROP, StatusesTest.CONTEXT)
                    .add(StatusesTest.TARGET_PROP, StatusesTest.URL)
                    .build().toString()
            )
        );
    }

    /**
     * StatusCreate can convert itself to JSON when it has no context.
     */
    @Test
    public void convertsToJsonWhenContextAbsent() {
        final String pending = "Kragle is drying...";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Statuses.StatusCreate(Status.State.PENDING)
                .withTargetUrl(Optional.of(StatusesTest.URL))
                .withDescription(pending)
                .json().toString(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(StatusesTest.STATE_PROP, "pending")
                    .add(StatusesTest.DESCRIPTION_PROP, pending)
                    .add(StatusesTest.TARGET_PROP, StatusesTest.URL)
                    .build().toString()
            )
        );
    }
}
