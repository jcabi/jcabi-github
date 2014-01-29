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
import javax.json.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link RtRepos}.
 *
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
public class RtReposITCase {
    /**
     * The key for name in JSON.
     */
    private static final String NAME_KEY = "name";

    /**
     * RtRepos create repository test.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public final void create() throws Exception {
        final String name = RandomStringUtils.randomNumeric(5);
        final Repos repos = RtReposITCase.github().repos();
        try {
            MatcherAssert.assertThat(
                repos.create(RtReposITCase.request(name)),
                Matchers.notNullValue()
            );
        } finally {
            final Coordinates.Simple coordinates = new Coordinates.Simple(
                RtReposITCase.github().users().self().login(), name
            );
            repos.remove(coordinates);
        }
    }

    /**
     * RtRepos should fail on creation of two repos with the same name.
     * @throws Exception If some problem inside
     */
    @Test(expected = AssertionError.class)
    public final void failsOnCreationOfTwoRepos() throws Exception {
        final String name = RandomStringUtils.randomNumeric(5);
        final Repos repos = RtReposITCase.github().repos();
        repos.create(RtReposITCase.request(name));
        try {
            repos.create(RtReposITCase.request(name));
        } finally {
            repos.remove(
                new Coordinates.Simple(
                    RtReposITCase.github().users().self().login(), name
                )
            );
        }
    }

    /**
     * Create and return JsonObject to test request.
     *
     * @param name Repo name
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject request(final String name) throws Exception {
        return Json.createObjectBuilder().add(NAME_KEY, name).build();
    }

    /**
     * Create and return repo to test.
     *
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Github github() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key);
    }
}
