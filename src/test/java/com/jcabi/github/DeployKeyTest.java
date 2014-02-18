/**
 * Copyright (c) 2012-2013, JCabi.com
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
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link DeployKey}.
 *
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (150 lines)
 */
public final class DeployKeyTest {

    /**
     * DeployKey.Smart can update the key value of DeployKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesKey() throws Exception {
        final DeployKey key = Mockito.mock(DeployKey.class);
        final String value = "sha-rsa BBB...";
        new DeployKey.Smart(key).key(value);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("key", value).build()
        );
    }

    /**
     * DeployKey.Smart can update the title property of DeployKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesTitle() throws Exception {
        final DeployKey key = Mockito.mock(DeployKey.class);
        final String prop = "octocat@octomac";
        new DeployKey.Smart(key).title(prop);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("title", prop).build()
        );
    }

}
