/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtUserEmails}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtUserEmailsTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void fetchesEmails() throws IOException {
        final String email = "test@email.com";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtUserEmails(
                new FakeRequest().withBody(
                    Json.createArrayBuilder()
                        .add(Json.createObjectBuilder().add("email", email))
                        .build().toString()
                )
            ).iterate().iterator().next(), Matchers.equalTo(email)
        );
    }

    @Test
    void addsEmails() throws IOException {
        final String email = "test1@email.com";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                String.format("[{\"email\":\"%s\"}]", email)
            )
        );
        container.start(RandomPort.port());
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                new RtUserEmails(
                    new ApacheRequest(container.home())
                ).add(Collections.singletonList(email)).iterator().next(),
                Matchers.equalTo(email)
            );
        } finally {
            container.stop();
        }
    }

    @Test
    void removesEmails() {
        final UserEmails emails = new RtUserEmails(
            new FakeRequest().withStatus(HttpURLConnection.HTTP_NO_CONTENT)
        );
        Assertions.assertDoesNotThrow(
            () -> emails.remove(Collections.singletonList("test2@email.com")),
            "Emails are not removed"
        );
    }
}
