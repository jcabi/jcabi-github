/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PublicKey;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPublicKey}.
 * @since 0.1
 */
public final class MkPublicKeyTest {

    /**
     * Json name of key.
     */
    public static final String KEY = "key";

    /**
     * MkPublicKey can be represented as JSON.
     */
    @Test
    public void canRetrieveAsJson() throws IOException {
        final String title = "Title1";
        final String key = "PublicKey1";
        final JsonObject json = new MkGitHub().users().add("john").keys()
            .create(title, key).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getString("id"),
            Matchers.equalTo("1")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getString("title"),
            Matchers.equalTo(title)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getString(MkPublicKeyTest.KEY),
            Matchers.equalTo(key)
        );
    }

    /**
     * MkPublicKey can accept a PATCH request.
     *
     */
    @Test
    public void canBePatched() throws IOException {
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

}
