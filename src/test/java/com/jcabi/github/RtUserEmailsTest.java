/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtUserEmails}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
public final class RtUserEmailsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    public void fetchesEmails() throws IOException {
        final String email = "test@email.com";
        final UserEmails emails = new RtUserEmails(
            new FakeRequest().withBody(
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder().add("email", email))
                    .build().toString()
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            emails.iterate().iterator().next(), Matchers.equalTo(email)
        );
    }

    @Test
    public void addsEmails() throws IOException {
        final String email = "test1@email.com";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                String.format("[{\"email\":\"%s\"}]", email)
            )
        );
        container.start(RandomPort.port());
        try {
            final UserEmails emails = new RtUserEmails(
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                emails.add(Collections.singletonList(email)).iterator().next(),
                Matchers.equalTo(email)
            );
        } finally {
            container.stop();
        }
    }

    @Test
    public void removesEmails() throws IOException {
        final UserEmails emails = new RtUserEmails(
            new FakeRequest().withStatus(HttpURLConnection.HTTP_NO_CONTENT)
        );
        emails.remove(Collections.singletonList("test2@email.com"));
    }

}
