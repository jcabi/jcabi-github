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
 * Test case for {@link RtBlobs}.
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class RtBlobsITCase {

    /**
     * RtBlobs can create a blob.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createsBlob() throws Exception {
        final Repos repos = repos();
        final Repo repo = repo(repos);
        try {
            final Blobs blobs = repo.git().blobs();
            final Blob blob = blobs.create(
                "Test Content", "utf-8"
            );
            MatcherAssert.assertThat(
                blob.sha(),
                Matchers.equalTo(blob.json().getString("sha"))
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }
    /**
     * RtBlobs can get a blob.
     * @throws Exception If something goes wrong
     */
    @Test
    public void getsBlob() throws Exception {
        final Repos repos = repos();
        final Repo repo = repo(repos);
        try {
            final Blobs blobs = repo.git().blobs();
            final String content = "Content of the blob";
            final String encoding = "base64";
            final Blob blob = blobs.create(
                content, encoding
            );
            MatcherAssert.assertThat(
                blobs.get(blob.sha()).json().getString("sha"),
                Matchers.equalTo(blob.sha())
            );
            MatcherAssert.assertThat(
                blobs.get(blob.sha()).json().getString("encoding"),
                Matchers.equalTo(encoding)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * Create repo for tests.
     * @param repos Repos
     * @return Repo
     * @throws java.io.IOException If an IO Exception occurs.
     */
    private static Repo repo(final Repos repos) throws IOException {
        return repos.create(
            Json.createObjectBuilder().add(
                "name", RandomStringUtils.randomAlphanumeric(Tv.TEN)
            ).add("auto_init", true).build()
        );
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
