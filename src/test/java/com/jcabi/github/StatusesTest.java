/**
 * Copyright (c) 2013-2024, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Statuses}.
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
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
