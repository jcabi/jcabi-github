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
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPublicKey}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
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
        final JsonObject json = new MkGithub().users().get("john").keys()
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
        final String title = RandomStringUtils.random(10);
        final String original = RandomStringUtils.random(10);
        final PublicKey key = new MkGithub().users().get("jeff")
            .keys().create(title, original);
        final String patch = String.format("%s_patch", original);
        key.patch(
            Json.createObjectBuilder().add(KEY, patch).build()
        );
        MatcherAssert.assertThat(
            key.json().getString(KEY),
            Matchers.equalTo(patch)
        );
    }

}
