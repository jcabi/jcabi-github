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
import java.io.IOException;
import java.net.HttpURLConnection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPublicKeys}.
 *
 */
public final class RtPublicKeysTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPublicKeys should be able to iterate its keys.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesKeys() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(key(1))
                        .add(key(2))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            MatcherAssert.assertThat(
                keys.iterate(),
                Matchers.<PublicKey>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtPublicKeys should be able to obtain a single key.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canFetchSingleKey() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    ""
                )
            ).start(this.resource.port())
        ) {
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            MatcherAssert.assertThat(
                keys.get(1),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * RtPublicKeys should be able to remove a key.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canRemoveKey() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    ""
                )
            ).start(this.resource.port())
        ) {
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            keys.remove(1);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/user/keys/1")
            );
            MatcherAssert.assertThat(
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
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED, key(1).toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPublicKeys keys = new RtPublicKeys(
                new ApacheRequest(container.home()),
                Mockito.mock(User.class)
            );
            MatcherAssert.assertThat(
                keys.create("theTitle", "theKey").number(),
                Matchers.is(1)
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/user/keys")
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.equalTo(
                    "{\"title\":\"theTitle\",\"key\":\"theKey\"}"
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
