/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PublicKey;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkPublicKey}.
 * @since 0.1
 */
final class MkPublicKeyTest {

    /**
     * Json name of key.
     */
    static final String KEY = "key";

    @Test
    void canRetrieveIdAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Key has a wrong id",
            MkPublicKeyTest.json().getString("id"),
            Matchers.equalTo("1")
        );
    }

    @Test
    void canRetrieveTitleAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Key has a wrong title",
            MkPublicKeyTest.json().getString("title"),
            Matchers.equalTo("Title1")
        );
    }

    @Test
    void canRetrieveKeyAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Key has a wrong body",
            MkPublicKeyTest.json().getString(MkPublicKeyTest.KEY),
            Matchers.equalTo("PublicKey1")
        );
    }

    @Test
    void canBePatched() throws IOException {
        final String original = "PublicKey2";
        final PublicKey key = new MkGitHub().users().add("jeff")
            .keys().create("Title2", original);
        final String patched = String.format("%s_patch", original);
        key.patch(
            Json.createObjectBuilder().add(MkPublicKeyTest.KEY, patched).build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            key.json().getString(MkPublicKeyTest.KEY),
            Matchers.equalTo(patched)
        );
    }

    private static JsonObject json() throws IOException {
        return new MkGitHub().users().add("john").keys()
            .create("Title1", "PublicKey1").json();
    }
}
