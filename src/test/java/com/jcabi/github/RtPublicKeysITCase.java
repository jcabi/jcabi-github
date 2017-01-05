/**
 * Copyright (c) 2013-2017, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import java.io.ByteArrayOutputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtPublicKeys}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
@OAuthScope(Scope.ADMIN_PUBLIC_KEY)
public class RtPublicKeysITCase {

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
            keys.iterate() ,
            Matchers.hasItem(key)
        );
        keys.remove(key.number());
        MatcherAssert.assertThat(
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
                keys.iterate(),
                Matchers.hasItem(key)
            );
            MatcherAssert.assertThat(
                key.user(),
                Matchers.equalTo(
                    keys.user()
                )
            );
        } finally {
            keys.remove(key.number());
        }
        MatcherAssert.assertThat(
            keys.iterate(),
            Matchers.not(Matchers.hasItem(key))
        );
    }

    /**
     * Generates a random public key for test.
     * @return The encoded SSH public key.
     * @throws Exception If a problem occurs.
     */
    private String key() throws Exception {
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
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).users().self().keys();
    }

}
