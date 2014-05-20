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
import java.io.IOException;
import javax.json.Json;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
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
     * RtReleaseAsset can fetch as JSON object.
     * @throws Exception if some problem inside
     */
    @Test
    public void fetchAsJSON() throws Exception {
        final Repos repos = repos();
        final Repo repo = repo(repos);
        try {
            final String name = RandomStringUtils.randomAlphanumeric(5);
            final Release release = repo.releases().create(name);
            try {
                MatcherAssert.assertThat(
                    release.json().getInt("id"),
                    Matchers.equalTo(release.number())
                );
            } finally {
                release.delete();
            }
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtReleaseAsset can execute patch request.
     * @throws Exception if some problem inside
     */
    @Test
    public void executePatchRequest() throws Exception {
        final Repos repos = repos();
        final Repo repo = repo(repos);
        try {
            final String rname = RandomStringUtils.randomAlphanumeric(5);
            final Release release = repo.releases().create(rname);
            final String name = "name";
            final String nvalue = RandomStringUtils.randomAlphanumeric(5);
            final String body = "body";
            final String bvalue = "Description of the release";
            try {
                release.patch(Json.createObjectBuilder().add(name, nvalue)
                    .add(body, bvalue).build()
                );
                MatcherAssert.assertThat(
                    release.json().getString(name),
                    Matchers.startsWith(nvalue)
                );
                MatcherAssert.assertThat(
                    release.json().getString(body),
                    Matchers.startsWith(bvalue)
                );
            } finally {
                release.delete();
            }
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtReleaseAsset can do delete operation.
     * @throws Exception If something goes wrong
     */
    @Test
    public void removesReleaseAsset() throws Exception {
        final Repos repos = repos();
        final Repo repo = repo(repos);
        try {
            final Releases releases = repo.releases();
            final String rname = RandomStringUtils.randomAlphanumeric(5);
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
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * Create repo with releases for tests.
     * @param repos Repos
     * @return Repo
     * @throws IOException If an IO Exception occurs.
     */
    private static Repo repo(final Repos repos) throws IOException {
        final Repo repo = repos.create(
            Json.createObjectBuilder().add(
                "name", RandomStringUtils.randomAlphanumeric(Tv.TEN)
            ).add("auto_init", true).build()
        );
        repo.releases().create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        return repo;
    }

    /**
     * Create repos of account with provided github key.
     * @return Repos
     */
    private static Repos repos() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).repos();
    }

}
