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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Release}.
 * @author Denis Anisimov (denis.nix.anisimov@gmail.com)
 * @version $Id$
 */
public final class ReleaseTest {

    /**
     * Release.Smart can fetch url properties of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesUrls() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String url = "http://url";
        final String htmlurl = "http://html_url";
        final String assetsurl = "http://assets_url";
        final String uploadurl = "http://upload_url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", url)
                .add("html_url", htmlurl)
                .add("assets_url", assetsurl)
                .add("upload_url", uploadurl)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.url().toString(),
            Matchers.equalTo(url)
        );
        MatcherAssert.assertThat(
            smart.htmlUrl().toString(),
            Matchers.equalTo(htmlurl)
        );
        MatcherAssert.assertThat(
            smart.assetsUrl().toString(),
            Matchers.equalTo(assetsurl)
        );
        MatcherAssert.assertThat(
            smart.uploadUrl().toString(),
            Matchers.equalTo(uploadurl)
        );
    }

    /**
     * Release.Smart returns correct number of Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void testId() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(1).when(release).number();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.number(),
            Matchers.equalTo(1)
        );
    }

    /**
     * Release.Smart can fetch other properties properties of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchProperties() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String tag = "v1.0.0";
        final String master = "master";
        final String name = "v1";
        final String description = "Description of the release";
        final String created = "2013-02-27T19:35:32Z";
        final String published = "2013-01-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("tag_name", tag)
                .add("target_commitish", master)
                .add("name", name)
                .add("body", description)
                .add("created_at", created)
                .add("published_at", published)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.tag(),
            Matchers.equalTo(tag)
        );
        MatcherAssert.assertThat(
            smart.commitish(),
            Matchers.equalTo(master)
        );
        MatcherAssert.assertThat(
            smart.name(),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            smart.body(),
            Matchers.equalTo(description)
        );
        MatcherAssert.assertThat(
            smart.createdAt(),
            Matchers.equalTo(new Github.Time(created).date())
        );
        MatcherAssert.assertThat(
            smart.publishedAt(),
            Matchers.equalTo(new Github.Time(published).date())
        );
    }
}
