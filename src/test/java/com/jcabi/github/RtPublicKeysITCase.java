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
import org.junit.Test;

/**
 * Test case for {@link RtPublicKeys}.
 */
@OAuthScope(OAuthScope.Scope.ADMIN_PUBLIC_KEY)
public final class RtPublicKeysITCase {

    /**
     * RtPublicKeys should be able to retrieve its keys.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public final void retrievesKeys() throws Exception {
        final PublicKeys keys = this.keys();
        final PublicKey key = keys.create("key", this.key());
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate(),
            Matchers.hasItem(key)
        );
        keys.remove(key.number());
    }

    /**
     * RtPublicKeys should be able to retrieve a single key.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public final void retrievesSingleKey() throws Exception {
        final PublicKeys keys = this.keys();
        final PublicKey key = keys.create("Title", this.key());
        MatcherAssert.assertThat(
            "Values are not equal",
            keys.get(key.number()),
            Matchers.equalTo(key)
        );
        keys.remove(key.number());
    }

    /**
     * RtPublicKeys should be able to remove a key.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public final void removesKey() throws Exception {
        final PublicKeys keys = this.keys();
        final PublicKey key = keys.create("", this.key());
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            keys.iterate() ,
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
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public final void createsKey() throws Exception {
        final PublicKeys keys = this.keys();
        // @checkstyle LineLength (1 line)
        final PublicKey key = keys.create("rsa", this.key());
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
    private String key() throws JSchException, IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            final KeyPair kpair = KeyPair.genKeyPair(new JSch(), KeyPair.DSA);
            kpair.writePublicKey(stream, "");
            kpair.dispose();
        } finally {
            stream.close();
        }
        return new String(stream.toByteArray());
    }

    /**
     * Create and return PublicKeys object to test.
     * @return PublicKeys
     */
    private PublicKeys keys() {
        return new GitHubIT().connect().users().self().keys();
    }

}
