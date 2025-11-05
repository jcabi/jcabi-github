/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PublicKey;
import com.jcabi.github.PublicKeys;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPublicKeys}.
 *
 */
public final class MkPublicKeysTest {

    /**
     * MkPublicKeys should be able to iterate its keys.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesKeys() throws Exception {
        final PublicKeys keys = new MkGithub().users().self().keys();
        final PublicKey key = keys.create("key", "ssh 1AA");
        MatcherAssert.assertThat(
            keys.iterate(),
            Matchers.hasItem(key)
        );
    }

    /**
     * MkPublicKeys should be able to retrieve a single key.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canFetchSingleKey() throws Exception {
        final PublicKeys keys = new MkGithub().users().add("jeff").keys();
        MatcherAssert.assertThat(
            keys.get(1),
            Matchers.notNullValue()
        );
    }

    /**
     * MkPublicKeys should be able to create a public key.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canCreatePublicKey() throws Exception {
        final PublicKeys keys = new MkGithub().users().add("john").keys();
        final PublicKey key = keys.create("Title1", "PublicKey1");
        MatcherAssert.assertThat(
            keys.get(key.number()),
            Matchers.equalTo(key)
        );
    }

    /**
     * MkPublicKeys should be able to remove a key.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canRemoveKey() throws Exception {
        final PublicKeys keys = new MkGithub().users().self().keys();
        final PublicKey key = keys.create("rsa", "rsa sh");
        MatcherAssert.assertThat(
            keys.iterate(),
            Matchers.hasItem(key)
        );
        keys.remove(key.number());
        MatcherAssert.assertThat(
            keys.iterate(),
            Matchers.not(Matchers.hasItem(key))
        );
    }

}
