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
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtUserEmails}.
 */
public final class RtUserEmailsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();
    /**
     * RtUserEmails can fetch emails.
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
            emails.iterate().iterator().next(), Matchers.equalTo(email)
        );
    }

    /**
     * RtUserEmails can add emails.
     */
    @Test
    public void addsEmails() throws IOException {
        final String email = "test1@email.com";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                String.format("[{\"email\":\"%s\"}]", email)
            )
        );
        container.start(this.resource.port());
        try {
            final UserEmails emails = new RtUserEmails(
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                emails.add(Collections.singletonList(email)).iterator().next(),
                Matchers.equalTo(email)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtUserEmails can remove emails.
     */
    @Test
    public void removesEmails() throws IOException {
        final UserEmails emails = new RtUserEmails(
            new FakeRequest().withStatus(HttpURLConnection.HTTP_NO_CONTENT)
        );
        emails.remove(Collections.singletonList("test2@email.com"));
    }

}
