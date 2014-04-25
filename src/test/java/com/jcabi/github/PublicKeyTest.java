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

import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link PublicKey}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (150 lines)
 */
public final class PublicKeyTest {

    /**
     * PublicKey.Smart can fetch the key value from PublicKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesKey() throws Exception {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String value = "sha-rsa AAA...";
        Mockito.doReturn(
            Json.createObjectBuilder().add("key", value).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            new PublicKey.Smart(key).key(),
            Matchers.is(value)
        );
    }

    /**
     * PublicKey.Smart can update the key value of PublicKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesKey() throws Exception {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String value = "sha-rsa BBB...";
        new PublicKey.Smart(key).key(value);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("key", value).build()
        );
    }

    /**
     * PublicKey.Smart can fetch URL property from PublicKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesUrl() throws Exception {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "https://api.github.com/user/keys/1";
        Mockito.doReturn(
            Json.createObjectBuilder().add("url", prop).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            new PublicKey.Smart(key).url().toString(),
            Matchers.is(prop)
        );
    }

    /**
     * PublicKey.Smart can fetch title property from PublicKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesTitle() throws Exception {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "octocat@octomac";
        Mockito.doReturn(
            Json.createObjectBuilder().add("title", prop).build()
        ).when(key).json();
        MatcherAssert.assertThat(
            new PublicKey.Smart(key).title(),
            Matchers.is(prop)
        );
    }

    /**
     * PublicKey.Smart can update the title property of PublicKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesTitle() throws Exception {
        final PublicKey key = Mockito.mock(PublicKey.class);
        final String prop = "octocat@octomac";
        new PublicKey.Smart(key).title(prop);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("title", prop).build()
        );
    }

}
