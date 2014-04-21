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
package com.jcabi.github.mock;

import com.jcabi.github.PublicKey;
import com.jcabi.github.PublicKeys;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPublicKeys}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
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
        final PublicKeys keys = new MkGithub().users().get("jeff").keys();
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
        final PublicKeys keys = new MkGithub().users().get("john").keys();
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
