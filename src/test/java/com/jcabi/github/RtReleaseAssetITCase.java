/**
 * Copyright (c) 2013-2014, jcabi.com
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

import com.jcabi.aspects.Tv;
import javax.json.Json;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link RtReleaseAsset}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (300 lines)
 */
public final class RtReleaseAssetITCase {

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
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
        repos = github.repos();
        repo = repos.create(
            Json.createObjectBuilder().add(
                "name", RandomStringUtils.randomAlphanumeric(Tv.TEN)
            ).add("auto_init", true).build()
        );
        repo.releases().create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
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
     * RtReleaseAsset can fetch as JSON object.
     * @throws Exception if some problem inside
     */
    @Test
    public void fetchAsJSON() throws Exception {
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final Release release = repo.releases().create(name);
        try {
            MatcherAssert.assertThat(
                release.json().getInt("id"),
                Matchers.equalTo(release.number())
            );
        } finally {
            release.delete();
        }
    }

    /**
     * RtReleaseAsset can execute patch request.
     * @throws Exception if some problem inside
     */
    @Test
    public void executePatchRequest() throws Exception {
        final Release release = repo.releases().create(
            String.format("v%s", RandomStringUtils.randomAlphanumeric(Tv.TEN))
        );
        final String desc = "Description of the release";
        try {
            release.patch(Json.createObjectBuilder().add("body", desc).build());
            MatcherAssert.assertThat(
                new Release.Smart(release).body(),
                Matchers.startsWith(desc)
            );
        } finally {
            release.delete();
        }
    }

    /**
     * RtReleaseAsset can do delete operation.
     * @throws Exception If something goes wrong
     */
    @Test
    public void removesReleaseAsset() throws Exception {
        final Releases releases = repo.releases();
        final String rname = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final Release release = releases.create(rname);
        try {
            MatcherAssert.assertThat(
                releases.get(release.number()),
                Matchers.notNullValue()
            );
        } finally {
            release.delete();
        }
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.not(Matchers.contains(release))
        );
    }

}
