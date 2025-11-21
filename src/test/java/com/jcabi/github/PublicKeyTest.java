/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link PublicKey}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (150 lines)
 */
final class PublicKeyTest {

    @Test
    void fetchesKey() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String value = "sha-rsa AAA...";
        Mockito.doReturn(
            Json.createObjectBuilder().add("key", value).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PublicKey.Smart(key).key(),
            Matchers.is(value)
        );
    }

    @Test
    void updatesKey() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String value = "sha-rsa BBB...";
        new PublicKey.Smart(key).key(value);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("key", value).build()
        );
    }

    @Test
    void fetchesUrl() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "https://api.github.com/user/keys/1";
        Mockito.doReturn(
            Json.createObjectBuilder().add("url", prop).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PublicKey.Smart(key).url().toString(),
            Matchers.is(prop)
        );
    }

    @Test
    void fetchesTitle() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "octocat@octomac";
        Mockito.doReturn(
            Json.createObjectBuilder().add("title", prop).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new PublicKey.Smart(key).title(),
            Matchers.is(prop)
        );
    }

    @Test
    void updatesTitle() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "octocat@octomac";
        new PublicKey.Smart(key).title(prop);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("title", prop).build()
        );
    }

}
