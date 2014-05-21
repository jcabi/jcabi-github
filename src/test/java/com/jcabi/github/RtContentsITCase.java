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
import javax.json.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (300 lines)
 */
public final class RtContentsITCase {

    /**
     * RtContents can fetch readme file.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchReadmeFiles() throws Exception {
        final Repos repos = github().repos();
        final Repo repo = RtContentsITCase.repo(repos);
        try {
            MatcherAssert.assertThat(
                repos.get(repo.coordinates()).contents().readme().path(),
                Matchers.equalTo("README.md")
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can get update file content.
     * @throws Exception If some problem inside
     */
    @Test
    public void canUpdateFileContent() throws Exception {
        final Repos repos = github().repos();
        final Repo repo = RtContentsITCase.repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        final String message = "commit message";
        final String text = "new content";
        try {
            final String path = RandomStringUtils.randomAlphabetic(Tv.TEN);
            final Content content = contents.create(
                this.jsonObject(
                    path, new String(
                        Base64.encodeBase64("init content".getBytes())
                    ),
                    message
                )
            );
            contents.update(
                path,
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message)
                    .add("content", Base64.encodeBase64String(text.getBytes()))
                    .add("sha", new Content.Smart(content).sha()).build()
            );
            MatcherAssert.assertThat(
                new String(
                    Base64.decodeBase64(
                        new Content.Smart(
                            contents.get(path, "master")
                        ).content()
                    )
                ),
                Matchers.equalTo(text)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can remove and throw an exception when get an absent content.
     * @throws Exception If some problem inside
     */
    @Test(expected = AssertionError.class)
    public void throwsWhenTryingToGetAnAbsentContent() throws Exception {
        final Repos repos = github().repos();
        final Repo repo = RtContentsITCase.repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        final String message = "commit message";
        try {
            final String path = RandomStringUtils.randomAlphabetic(Tv.TEN);
            final Content content = contents.create(
                this.jsonObject(
                    path, new String(
                        Base64.encodeBase64("first content".getBytes())
                    ),
                    message
                )
            );
            contents.remove(
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message)
                    .add("sha", new Content.Smart(content).sha()).build()
            );
            contents.get(path, "master");
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can create file content.
     * @throws Exception If some problem inside
     */
    @Test
    public void canCreateFileContent() throws Exception {
        final Repos repos = github().repos();
        final Repo repo = RtContentsITCase.repo(repos);
        try {
            final String path = RandomStringUtils.randomAlphabetic(Tv.TEN);
            MatcherAssert.assertThat(
                repos.get(repo.coordinates()).contents().create(
                    this.jsonObject(
                        path, new String(
                            Base64.encodeBase64("some content".getBytes())
                        ), "theMessage"
                    )
                ).path(),
                Matchers.equalTo(path)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can get content.
     * @throws Exception If some problem inside
     */
    @Test
    public void getContent() throws Exception {
        final Repos repos = github().repos();
        final Repo repo = RtContentsITCase.repo(repos);
        try {
            final String path = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final String message = String.format("testMessage");
            final String cont = new String(
                Base64.encodeBase64(
                    String.format("content%d", System.currentTimeMillis())
                        .getBytes()
                )
            );
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(this.jsonObject(path, cont, message));
            final Content content = contents.get(path, "master");
            MatcherAssert.assertThat(
                content.path(),
                Matchers.equalTo(path)
            );
            MatcherAssert.assertThat(
                new Content.Smart(content).content(),
                Matchers.equalTo(String.format("%s\n", cont))
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * Create and return JsonObject of content.
     * @param path Content's path
     * @param cont Content's Base64 string
     * @param message Message
     * @return JsonObject
     */
    private JsonObject jsonObject(
        final String path, final String cont, final String message
    ) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message)
            .add("content", cont)
            .add("ref", "master")
            .build();
    }

    /**
     * Create and return repo to test.
     *
     * @param repos Repos
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo(final Repos repos) throws Exception {
        return repos.create(
            Json.createObjectBuilder().add(
                "name", RandomStringUtils.randomAlphanumeric(Tv.TEN)
            ).add("auto_init", true).build()
        );
    }

    /**
     * Create and return github to test.
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
