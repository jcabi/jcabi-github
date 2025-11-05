/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PublicKey;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPublicKey}.
 *
 */
public final class MkPublicKeyTest {

    /**
     * Json name of key.
     */
    public static final String KEY = "key";

    /**
     * MkPublicKey can be represented as JSON.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canRetrieveAsJson() throws Exception {
        final String title = "Title1";
        final String key = "PublicKey1";
        final JsonObject json = new MkGithub().users().add("john").keys()
            .create(title, key).json();
        MatcherAssert.assertThat(
            json.getString("id"),
            Matchers.equalTo("1")
        );
        MatcherAssert.assertThat(
            json.getString("title"),
            Matchers.equalTo(title)
        );
        MatcherAssert.assertThat(
            json.getString(KEY),
            Matchers.equalTo(key)
        );
    }

    /**
     * MkPublicKey can accept a PATCH request.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canBePatched() throws Exception {
        final String original = "PublicKey2";
        final PublicKey key = new MkGithub().users().add("jeff")
            .keys().create("Title2", original);
        final String patched = String.format("%s_patch", original);
        key.patch(
            Json.createObjectBuilder().add(KEY, patched).build()
        );
        MatcherAssert.assertThat(
            key.json().getString(KEY),
            Matchers.equalTo(patched)
        );
    }

}
