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
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtForks}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public class RtForksITCase {

    /**
     * RtForks should be able to iterate its forks.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public final void retrievesForks() throws Exception {
        final Iterable<Fork> forks = RtForksITCase.repo().forks()
            .iterate("newest");
        MatcherAssert.assertThat(forks, Matchers.notNullValue());
        int count = 0;
        for (final Fork fork : forks) {
            MatcherAssert.assertThat(fork, Matchers.notNullValue());
            count += 1;
        }
        MatcherAssert.assertThat(
            "Test repo should have at leas 1 fork", count,
            Matchers.greaterThan(0)
        );
    }

    /**
     * RtForks should be able to create a new fork.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public final void createsFork() throws Exception {
        final String organization = System.getProperty(
            "failsafe.github.organization"
        );
        Assume.assumeThat(organization, Matchers.notNullValue());
        final Repo repo = RtForksITCase.repos().create(
            Json.createObjectBuilder().add(
                // @checkstyle MagicNumber (1 line)
                "name", RandomStringUtils.randomNumeric(5)
            ).build()
        );
        try {
            final Fork fork = repo.forks().create(organization);
            MatcherAssert.assertThat(fork, Matchers.notNullValue());
        } finally {
            RtForksITCase.repos().remove(repo.coordinates());
        }
    }

    /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        return RtForksITCase.repos().get(RtForksITCase.coordinates());
    }

    /**
     * Returns the authentication key, passed as system property
     * -Dfailsafe.github.key.
     * @return The key.
     */
    private static String key() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return key;
    }

    /**
     * Returns github repos.
     * @return Github repos.
     */
    private static Repos repos() {
        return new RtGithub(RtForksITCase.key()).repos();
    }

    /**
     * Create and return repo coordinates to test on.
     * @return Coordinates
     */
    private static Coordinates coordinates() {
        return new Coordinates.Simple(
            System.getProperty("failsafe.github.repo")
        );
    }

}
