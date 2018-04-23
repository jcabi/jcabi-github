/**
 * Copyright (c) 2013-2017, jcabi.com
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

import com.jcabi.github.Github;
import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkRelease}.
 *
 * @author Denis Anisimov (denis.nix.anisimov@gmail.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class MkReleaseTest {
    /**
     * Check if a release can be deleted.
     * @throws Exception If any problems occur.
     */
    @Test
    public void canDeleteRelease() throws Exception {
        final Releases releases = MkReleaseTest.releases();
        final Release release = releases.create("v1.0");
        release.delete();
        MatcherAssert.assertThat(
            releases.iterate().iterator().hasNext(),
            Matchers.is(false)
        );
    }

    /**
     * Smart decorator returns url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.url().toString(),
            Matchers.equalTo(this.value(release, "url"))
        );
    }

    /**
     * Smart decorator returns assets url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetAssetsUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.assetsUrl().toString(),
            Matchers.equalTo(this.value(release, "assets_url"))
        );
    }

    /**
     * Smart decorator returns html url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetHtmlUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.htmlUrl().toString(),
            Matchers.equalTo(this.value(release, "html_url"))
        );
    }

    /**
     * Smart decorator returns upload url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetUploadUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.uploadUrl().toString(),
            Matchers.equalTo(this.value(release, "upload_url"))
        );
    }

    /**
     * Smart decorator returns tag name.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetTag() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.tag(),
            Matchers.equalTo(this.value(release, "tag_name"))
        );
    }

    /**
     * Smart decorator returns target commitish.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetCommitish() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.commitish(),
            Matchers.equalTo(this.value(release, "target_commitish"))
        );
    }

    /**
     * Smart decorator returns name.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetName() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.name(),
            Matchers.equalTo(this.value(release, "name"))
        );
    }

    /**
     * Smart decorator returns body.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetBody() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.body(),
            Matchers.equalTo(this.value(release, "body"))
        );
    }

    /**
     * Smart decorator returns created date.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetCreatedAt() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.createdAt(),
            Matchers.equalTo(new Github.Time(this.value(release, "created_at"))
                .date()
            )
        );
    }

    /**
     * Smart decorator returns prerelease.
     * @throws Exception If some problem inside
     */
    @Test
    public void prerelease() throws Exception {
        final Release release = MkReleaseTest.release();
        release.patch(
            Json.createObjectBuilder().add("prerelease", true).build()
        );
        MatcherAssert.assertThat(
            new Release.Smart(release).prerelease(),
            Matchers.is(true)
        );
    }

    /**
     * Smart decorator returns published date.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetPublishedAt() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.publishedAt(),
            Matchers.equalTo(
                new Github.Time(this.value(release, "published_at")).date()
            )
        );
    }

    /**
     * Returns string property value.
     * @param release Release
     * @param name Property name
     * @return Value as a string
     * @throws IOException If some problem inside
     */
    private String value(final Release release, final String name)
        throws IOException {
        final JsonValue jsonValue = release.json().get(name);
        String result = null;
        if (jsonValue instanceof JsonString) {
            result = ((JsonString) jsonValue).getString();
        }
        return result;
    }

    /**
     * Create a release to work with.
     * @return Release
     * @throws Exception If some problem inside
     */
    private static Release release() throws Exception {
        return MkReleaseTest.releases().create("v1");
    }

    /**
     * Creates a Releases instance set to work with.
     * @return A test Releases instance.
     * @throws IOException if any I/O problems occur.
     */
    private static Releases releases() throws IOException {
        return new MkGithub().randomRepo().releases();
    }
}
