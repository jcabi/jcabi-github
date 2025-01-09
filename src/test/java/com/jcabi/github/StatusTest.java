/**
 * Copyright (c) 2013-2025, jcabi.com
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
import com.jcabi.github.mock.MkGithub;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Status}.
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 * @since 0.24
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class StatusTest {
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
    public void fetchesCommit() throws Exception {
        final Commit cmmt = StatusTest.commit();
        MatcherAssert.assertThat(
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
    public void fetchesId() throws Exception {
        final int ident = 777;
        MatcherAssert.assertThat(
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
    public void fetchesUrl() throws Exception {
        final String url = "http://api.jcabi-github.invalid/wherever";
        MatcherAssert.assertThat(
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
    public void fetchesErrorState() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesFailureState() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesPendingState() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesSuccessState() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesPresentTargetUrl() throws Exception {
        final String url = "http://api.jcabi-github.invalid/good-luck";
        MatcherAssert.assertThat(
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
    public void fetchesAbsentTargetUrl() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesNullTargetUrl() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesPresentDescription() throws Exception {
        final String description = "Mostly harmless";
        MatcherAssert.assertThat(
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
    public void fetchesAbsentDescription() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesNullDescription() throws Exception {
        MatcherAssert.assertThat(
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
    public void fetchesContext() throws Exception {
        final String context = "jcabi/github/tester";
        MatcherAssert.assertThat(
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
    public void fetchesCreatedAt() throws Exception {
        final String when = "2015-02-27T19:35:32Z";
        MatcherAssert.assertThat(
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().add("created_at", when).build()
                )
            ).createdAt(),
            Matchers.equalTo(new Github.Time(when).date())
        );
    }

    /**
     * Status.Smart can fetch its last-updated-at timestamp.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesUpdatedAt() throws Exception {
        final String when = "2013-02-27T19:35:32Z";
        MatcherAssert.assertThat(
            new Status.Smart(
                new RtStatus(
                    StatusTest.commit(),
                    Json.createObjectBuilder().add("updated_at", when).build()
                )
            ).updatedAt(),
            Matchers.equalTo(new Github.Time(when).date())
        );
    }

    /**
     * Status.Smart can fetch its creator.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesCreator() throws Exception {
        final String login = "bob";
        MatcherAssert.assertThat(
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
     * @throws Exception If some problem inside
     */
    private static Commit commit() throws Exception {
        return new MkGithub().randomRepo().git().commits()
            .get("d288364af5028c72e2a2c91c29343bae11fffcbe");
    }
}
