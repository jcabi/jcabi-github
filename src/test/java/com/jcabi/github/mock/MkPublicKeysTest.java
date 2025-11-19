/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PublicKey;
import com.jcabi.github.PublicKeys;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPublicKeys}.
 * @since 0.1
 */
public final class MkPublicKeysTest {

    @Test
    public void retrievesKeys() throws IOException {
        final PublicKeys keys = new MkGitHub().users().self().keys();
        final PublicKey key = keys.create("key", "ssh 1AA");
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate(),
            Matchers.hasItem(key)
        );
    }

    @Test
    public void canFetchSingleKey() throws IOException {
        final PublicKeys keys = new MkGitHub().users().add("jeff").keys();
        MatcherAssert.assertThat(
            "Value is null",
            keys.get(1),
            Matchers.notNullValue()
        );
    }

    @Test
    public void canCreatePublicKey() throws IOException {
        final PublicKeys keys = new MkGitHub().users().add("john").keys();
        final PublicKey key = keys.create("Title1", "PublicKey1");
        MatcherAssert.assertThat(
            "Values are not equal",
            keys.get(key.number()),
            Matchers.equalTo(key)
        );
    }

    @Test
    public void canRemoveKey() throws IOException {
        final PublicKeys keys = new MkGitHub().users().self().keys();
        final PublicKey key = keys.create("rsa", "rsa sh");
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate(),
            Matchers.hasItem(key)
        );
        keys.remove(key.number());
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate(),
            Matchers.not(Matchers.hasItem(key))
        );
    }

}
