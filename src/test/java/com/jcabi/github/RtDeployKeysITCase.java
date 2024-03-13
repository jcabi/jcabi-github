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
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import java.io.ByteArrayOutputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtDeployKeys}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@OAuthScope(Scope.ADMIN_PUBLIC_KEY)
public final class RtDeployKeysITCase {

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = new RepoRule().repo(repos);
    }

    /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }
    /**
     * RtDeployKeys can iterate deploy keys.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchAllDeployKeys() throws Exception {
        final DeployKeys keys = repo.keys();
        final String title = "Test Iterate Key";
        final DeployKey key = keys.create(title, key());
        try {
            MatcherAssert.assertThat(
                keys.iterate(),
                Matchers.hasItem(key)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can create a deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createsDeployKey() throws Exception {
        final DeployKeys keys = repo.keys();
        final String title = "Test Create Key";
        final DeployKey key = keys.create(title, key());
        try {
            MatcherAssert.assertThat(
                new DeployKey.Smart(key).title(),
                Matchers.is(title)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can get a single deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    public void getsDeployKey() throws Exception {
        final DeployKeys keys = repo.keys();
        final String title = "Test Get Key";
        final DeployKey key = keys.create(title, key());
        try {
            MatcherAssert.assertThat(
                keys.get(key.number()),
                Matchers.is(key)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can remove a deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    public void removesDeployKey() throws Exception {
        final DeployKeys keys = repo.keys();
        final String title = "Test Remove Key";
        final DeployKey key = keys.create(title, key());
        try {
            MatcherAssert.assertThat(
                keys.get(key.number()),
                Matchers.notNullValue()
            );
        } finally {
            key.remove();
        }
        MatcherAssert.assertThat(
            keys.iterate(),
            Matchers.not(Matchers.contains(key))
        );
    }

    /**
     * Generates a random public key for test.
     *
     * @return The encoded SSH public key.
     * @throws Exception If a problem occurs.
     */
    private static String key() throws Exception {
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
}
