/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPublicKeys}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtPublicKeysTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void retrievesKeys() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtPublicKeysTest.key(1))
                        .add(RtPublicKeysTest.key(2))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtPublicKeys(
                    new ApacheRequest(container.home()),
                    Mockito.mock(User.class)
                ).iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void canFetchSingleKey() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    ""
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Value is null",
                new RtPublicKeys(
                    new ApacheRequest(container.home()),
                    Mockito.mock(User.class)
                ).get(1),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void removesKeyAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtPublicKeysTest.keys(container).remove(1);
            MatcherAssert.assertThat(
                "Key is removed at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/user/keys/1")
            );
        }
    }

    @Test
    void removesKeyWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtPublicKeysTest.keys(container).remove(1);
            MatcherAssert.assertThat(
                "Key is not removed with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void canCreatePublicKey() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPublicKeysTest.created())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created key has a wrong number",
                RtPublicKeysTest.keys(container)
                    .create("theTitle", "theKey").number(),
                Matchers.is(1)
            );
        }
    }

    @Test
    void createsKeyAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPublicKeysTest.created())
                .start(RandomPort.port())
        ) {
            RtPublicKeysTest.keys(container).create("theTitle", "theKey");
            MatcherAssert.assertThat(
                "Key is created at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/user/keys")
            );
        }
    }

    @Test
    void sendsKeyWhileCreating() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPublicKeysTest.created())
                .start(RandomPort.port())
        ) {
            RtPublicKeysTest.keys(container).create("theTitle", "theKey");
            MatcherAssert.assertThat(
                "Key is not sent while creating",
                container.take().body(),
                Matchers.equalTo(
                    "{\"title\":\"theTitle\",\"key\":\"theKey\"}"
                )
            );
        }
    }

    private static RtPublicKeys keys(final MkContainer container)
        throws IOException {
        return new RtPublicKeys(
            new ApacheRequest(container.home()),
            Mockito.mock(User.class)
        );
    }

    private static MkAnswer created() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            RtPublicKeysTest.key(1).toString()
        );
    }

    private static JsonObject key(final int number) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("key", "ssh-rsa AAA")
            .build();
    }
}
