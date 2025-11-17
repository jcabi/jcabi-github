/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link PublicKey}.
 *
 * @checkstyle MultipleStringLiterals (150 lines)
 */
public final class PublicKeyTest {

    /**
     * PublicKey.Smart can fetch the key value from PublicKey.
     */
    @Test
    public void fetchesKey() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String value = "sha-rsa AAA...";
        Mockito.doReturn(
            Json.createObjectBuilder().add("key", value).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            new PublicKey.Smart(key).key(),
            Matchers.is(value)
        );
    }

    /**
     * PublicKey.Smart can update the key value of PublicKey.
     */
    @Test
    public void updatesKey() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String value = "sha-rsa BBB...";
        new PublicKey.Smart(key).key(value);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("key", value).build()
        );
    }

    /**
     * PublicKey.Smart can fetch URL property from PublicKey.
     */
    @Test
    public void fetchesUrl() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "https://api.github.com/user/keys/1";
        Mockito.doReturn(
            Json.createObjectBuilder().add("url", prop).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            new PublicKey.Smart(key).url().toString(),
            Matchers.is(prop)
        );
    }

    /**
     * PublicKey.Smart can fetch title property from PublicKey.
     */
    @Test
    public void fetchesTitle() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "octocat@octomac";
        Mockito.doReturn(
            Json.createObjectBuilder().add("title", prop).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            new PublicKey.Smart(key).title(),
            Matchers.is(prop)
        );
    }

    /**
     * PublicKey.Smart can update the title property of PublicKey.
     */
    @Test
    public void updatesTitle() throws IOException {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "octocat@octomac";
        new PublicKey.Smart(key).title(prop);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("title", prop).build()
        );
    }

}
