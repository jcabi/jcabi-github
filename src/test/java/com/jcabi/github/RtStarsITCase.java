/**
 * Copyright (c) 2013-2015, jcabi.com
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

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test case for {@link RtStars}.
 *
 * @author Artem Nakonechny (wentwogcq@gmail.com)
 * @version $Id$
 */
public final class RtStarsITCase {
    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up tests.
     * @throws IOException If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws IOException  {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
        repos = github.repos();
        repo = new RepoRule().repo(repos);
    }

    /**
     * Set up tests.
     * @throws IOException If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws IOException  {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtStars can star, unstar and check whether the github repository is
     * starred.
     * @throws IOException If some errors occurred.
     */
    @Test
    public void starsUnstarsChecksStar() throws IOException {
        MatcherAssert.assertThat(
            repo.stars().starred(),
            Matchers.equalTo(false)
        );
        repo.stars().star();
        MatcherAssert.assertThat(
            repo.stars().starred(),
            Matchers.equalTo(true)
        );
        repo.stars().unstar();
        MatcherAssert.assertThat(
            repo.stars().starred(),
            Matchers.equalTo(false)
        );
    }
}
