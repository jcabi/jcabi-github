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
package com.jcabi.github.mock;

import com.jcabi.github.DeployKey;
import com.jcabi.github.DeployKeys;
import com.jcabi.github.Repo;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkDeployKeys}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
public final class MkDeployKeysTest {
    /**
     * MkDeployKeys can fetch empty list of deploy keys.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfDeployKeys() throws Exception {
        final DeployKeys deployKeys = MkDeployKeysTest.repo().keys();
        MatcherAssert.assertThat(
            deployKeys.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkDeployKeys can fetch a single deploy key.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchSingleDeployKey() throws Exception {
        final DeployKeys keys = MkDeployKeysTest.repo().keys();
        final DeployKey key = keys.create("Title", "Key");
        MatcherAssert.assertThat(keys.get(key.number()), Matchers.equalTo(key));
    }

    /**
     * MkDeployKeys can create a deploy key.
     * @throws Exception If some problem inside.
     */
    @Test
    public void canCreateDeployKey() throws Exception {
        final DeployKeys keys = MkDeployKeysTest.repo().keys();
        final DeployKey key = keys.create("Title1", "Key1");
        MatcherAssert.assertThat(key, Matchers.equalTo(keys.get(key.number())));
    }

    /**
     * Create a repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }
}
