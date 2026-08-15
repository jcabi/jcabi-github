/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PublicKey;
import com.jcabi.github.PublicKeys;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkPublicKeys}.
 * @since 0.1
 */
final class MkPublicKeysTest {

    @Test
    void retrievesKeys() throws IOException {
        final PublicKeys keys = new MkGitHub().users().self().keys();
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate(),
            Matchers.hasItem(keys.create("key", "ssh 1AA"))
        );
    }

    @Test
    void canFetchSingleKey() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().users().add("jeff").keys().get(1),
            Matchers.notNullValue()
        );
    }

    @Test
    void canCreatePublicKey() throws IOException {
        final PublicKeys keys = new MkGitHub().users().add("john").keys();
        final PublicKey key = keys.create("Title1", "PublicKey1");
        MatcherAssert.assertThat(
            "Values are not equal",
            keys.get(key.number()),
            Matchers.equalTo(key)
        );
    }

    @Test
    void canRemoveKey() throws IOException {
        final PublicKeys keys = new MkGitHub().users().self().keys();
        final PublicKey key = keys.create("rsa", "rsa sh");
        keys.remove(key.number());
        MatcherAssert.assertThat(
            "Removed key is still in the collection",
            keys.iterate(),
            Matchers.not(Matchers.hasItem(key))
        );
    }
}
