/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPublicKeys}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
public final class RtPublicKeysTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */


    @Test
    public void retrievesKeys() throws IOException {
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
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                keys.iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    public void canFetchSingleKey() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    ""
                )
            ).start(RandomPort.port())
        ) {
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            MatcherAssert.assertThat(
                "Value is null",
                keys.get(1),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    public void canRemoveKey() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    ""
                )
            ).start(RandomPort.port())
        ) {
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            keys.remove(1);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "String does not end with expected value",
                query.uri().toString(),
                Matchers.endsWith("/user/keys/1")
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    /**
     * RtPublicKeys can create a key.
     * @throws IOException If some problem inside.
     */
    @Test
    public void canCreatePublicKey() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED, RtPublicKeysTest.key(1).toString()
                )
            ).start(RandomPort.port())
        ) {
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                keys.create("theTitle", "theKey").number(),
                Matchers.is(1)
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "String does not end with expected value",
                query.uri().toString(),
                Matchers.endsWith("/user/keys")
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.body(),
                Matchers.equalTo(
                    "{\"title\":\"theTitle\", \"key\":\"theKey\"}"
                )
            );
            container.stop();
        }
    }

    /**
     * Create and return key to test.
     * @param number Public Key Id
     * @return JsonObject
     */
    private static JsonObject key(final int number) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("key", "ssh-rsa AAA")
            .build();
    }
}
