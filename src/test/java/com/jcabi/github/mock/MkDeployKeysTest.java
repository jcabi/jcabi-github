/**
 * Copyright (c) 2013-2024, jcabi.com
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
        final DeployKeys deployKeys = new MkGithub().randomRepo().keys();
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
        final DeployKeys keys = new MkGithub().randomRepo().keys();
        final DeployKey key = keys.create("Title", "Key");
        MatcherAssert.assertThat(keys.get(key.number()), Matchers.equalTo(key));
    }

    /**
     * MkDeployKeys can create a deploy key.
     * @throws Exception If some problem inside.
     */
    @Test
    public void canCreateDeployKey() throws Exception {
        final DeployKeys keys = new MkGithub().randomRepo().keys();
        final DeployKey key = keys.create("Title1", "Key1");
        MatcherAssert.assertThat(key, Matchers.equalTo(keys.get(key.number())));
    }

    /**
     * MkDeployKeys can create distinct deploy keys.
     * Reproduces bug described in issue #346.
     * @throws Exception If some problem inside.
     */
    @Test
    public void canCreateDistinctDeployKeys() throws Exception {
        final DeployKeys keys = new MkGithub().randomRepo().keys();
        final DeployKey first = keys.create("Title2", "Key2");
        final DeployKey second = keys.create("Title3", "Key3");
        MatcherAssert.assertThat(
            first,
            Matchers.not(Matchers.is(second))
        );
        MatcherAssert.assertThat(
            first.number(),
            Matchers.not(Matchers.is(second.number()))
        );
    }

    /**
     * MkDeployKeys can be represented in JSON format.
     * Reproduces bug described in issue #346.
     * @throws Exception If some problem inside.
     */
    @Test
    public void canRepresentAsJson() throws Exception {
        final DeployKeys keys = new MkGithub().randomRepo().keys();
        final DeployKey first = keys.create("Title4", "Key4");
        MatcherAssert.assertThat(
            first.json().toString(),
            Matchers.allOf(
                Matchers.containsString("\"title\":\"Title4\""),
                Matchers.containsString("\"key\":\"Key4\"")
            )
        );
    }
}
