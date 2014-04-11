/**
 * Copyright (c) 2013-2014, JCabi.com
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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtPublicKeys}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @todo #1 RtPublicKeysITCase is disabled since it doesn't work
 *  with real Github account. The problem is that guthub can't remove
 *  keys properly, so we forced to generate valid key each new run
 *  Let's fix it and remove all Ignore annotations from all its methods.
 */
public class RtPublicKeysITCase {

    /**
     * RtPublicKeys should be able to retrieve its keys.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    @Ignore
    public final void retrievesKeys() throws Exception {
        final PublicKeys keys = this.keys();
        final PublicKey key = keys.create("key", "ssh 1AA");
        MatcherAssert.assertThat(
            keys.iterate(),
            Matchers.hasItem(key)
        );
        keys.remove(key.number());
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

    /**
     * RtPublicKeys should be able to retrieve a single key.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    @Ignore
    public final void retrievesSingleKey() throws Exception {
        final PublicKeys keys = this.keys();
        final PublicKey key = keys.create("Title", "Key");
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
    @Ignore
    public final void removesKey() throws Exception {
        final PublicKeys keys = this.keys();
        final PublicKey key = keys.create("", "");
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
    @Ignore
    public final void createsKey() throws Exception {
        final PublicKeys keys = this.keys();
        // @checkstyle LineLength (1 line)
        final PublicKey key = keys.create("rsa", "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDS+TF7+bae4UKj6nec1oipiP9Ysc6mBPszB80z13tMZBlsPCOiLVAMO2ER/wpnKHd/VylmYr5c6wc3kSj88846VHUhQDN7fLd/km06KTdW4+9db7HBfvr0063eDdi1lg8jlnccegeeqKsG39+iVQban7ugcPyJtjQE9k7JjYBT+SOgupWkYPVO+5Z3xF6VJL8gUTIMgoovgTabFx60t5h5UPtNaGbdcSlHhLOlWn8I7tHvwbYdhZVqlCC450rieXo8PpjndG3crcuHPZPDVSSXyqRpguIxVEVjXd3B/0vrhXJQJC4u0ukOOytLNL6Gzz3oK7SIB0mqWJ4Mo0Wp+zeX jac.wshmstr@gmail.com");
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

}
