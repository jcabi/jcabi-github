/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtPublicKeys}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.ADMIN_PUBLIC_KEY)
final class RtPublicKeysITCase {

    /**
     * RtPublicKeys should be able to retrieve its keys.
     * @throws Exception If a problem occurs.
     */
    @Test
    void retrievesKeys() throws Exception {
        final PublicKeys keys = RtPublicKeysITCase.keys();
        final PublicKey key = keys.create("key", RtPublicKeysITCase.key());
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate(),
            Matchers.hasItem(key)
        );
        keys.remove(key.number());
    }

    /**
     * RtPublicKeys should be able to retrieve a single key.
     * @throws Exception If a problem occurs.
     */
    @Test
    void retrievesSingleKey() throws Exception {
        final PublicKeys keys = RtPublicKeysITCase.keys();
        final PublicKey key = keys.create("Title", RtPublicKeysITCase.key());
        MatcherAssert.assertThat(
            "Values are not equal",
            keys.get(key.number()),
            Matchers.equalTo(key)
        );
        keys.remove(key.number());
    }

    /**
     * RtPublicKeys should be able to remove a key.
     * @throws Exception If a problem occurs.
     */
    @Test
    void removesKey() throws Exception {
        final PublicKeys keys = RtPublicKeysITCase.keys();
        final PublicKey key = keys.create("", RtPublicKeysITCase.key());
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

    /**
     * RtPublicKeys should be able to create a key.
     * @throws Exception If a problem occurs.
     */
    @Test
    void createsKey() throws Exception {
        final PublicKeys keys = RtPublicKeysITCase.keys();
        // @checkstyle LineLength (1 line)
        final PublicKey key = keys.create("rsa", RtPublicKeysITCase.key());
        try {
            MatcherAssert.assertThat(
                "Collection does not contain expected item",
                keys.iterate(),
                Matchers.hasItem(key)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                key.user(),
                Matchers.equalTo(
                    keys.user()
                )
            );
        } finally {
            keys.remove(key.number());
        }
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate(),
            Matchers.not(Matchers.hasItem(key))
        );
    }

    /**
     * Generates a random public key for test.
     * @return The encoded SSH public key.
     */
    private static String key() throws JSchException, IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            final KeyPair kpair = KeyPair.genKeyPair(new JSch(), KeyPair.DSA);
            kpair.writePublicKey(stream, "");
            kpair.dispose();
            return new String(stream.toByteArray());
        }
    }

    /**
     * Create and return PublicKeys object to test.
     * @return PublicKeys
     */
    private static PublicKeys keys() {
        return GitHubIT.connect().users().self().keys();
    }

}
