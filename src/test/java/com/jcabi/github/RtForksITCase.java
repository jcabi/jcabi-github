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
            final Iterable<Fork> forks = repo.forks().iterate("newest");
            MatcherAssert.assertThat(forks, Matchers.notNullValue());
            MatcherAssert.assertThat(
                forks,
                Matchers.not(Matchers.emptyIterable())
            );
            MatcherAssert.assertThat(
                forks,
                Matchers.contains(fork)
            );
        } finally {
            RtForksITCase.repos().remove(repo.coordinates());
        }
    }

    /**
     * Returns github repos.
     * @return Github repos.
     */
    private static Repos repos() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).repos();
    }

}
