/**
 * Copyright (c) 2013-2023, jcabi.com
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
import javax.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Release}.
 * @author Denis Anisimov (denis.nix.anisimov@gmail.com)
 * @checkstyle MultipleStringLiteralsCheck (400 lines)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class ReleaseTest {

    /**
     * Release.Smart can fetch url properties of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesUrls() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String url = "http://url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", url)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.url().toString(),
            Matchers.equalTo(url)
        );
    }

    /**
     * Release.Smart can fetch html url properties of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesHtmlUrls() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String htmlurl = "http://html_url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("html_url", htmlurl)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.htmlUrl().toString(),
            Matchers.equalTo(htmlurl)
        );
    }

    /**
     * Release.Smart can fetch assets url properties of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesAssetsHtmlUrls() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String assetsurl = "http://assets_url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("assets_url", assetsurl)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.assetsUrl().toString(),
            Matchers.equalTo(assetsurl)
        );
    }

    /**
     * Release.Smart can fetch upload url properties of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesUploadHtmlUrls() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String uploadurl = "http://upload_url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("upload_url", uploadurl)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.uploadUrl().toString(),
            Matchers.equalTo(uploadurl)
        );
    }

    /**
     * Release.Smart returns correct number of Release.
     */
    @Test
    public void testId() {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(1).when(release).number();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.number(),
            Matchers.equalTo(1)
        );
    }

    /**
     * Release.Smart can fetch tag of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchTag() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String tag = "v1.0.0";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("tag_name", tag)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.tag(),
            Matchers.equalTo(tag)
        );
    }

    /**
     * Release.Smart can fetch commitish of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchProperties() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String master = "master";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("target_commitish", master)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.commitish(),
            Matchers.equalTo(master)
        );
    }

    /**
     * Release.Smart can fetch name of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchName() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String name = "v1";
        // @checkstyle MultipleStringLiterals (3 lines)
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", name)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.hasName(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            smart.name(),
            Matchers.equalTo(name)
        );
    }

    /**
     * Release.Smart can determine if the release does not have a name
     * (NULL json value).
     * @throws Exception If some problem inside
     */
    @Test
    public void incidatesNoName() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", JsonValue.NULL)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.hasName(),
            Matchers.is(false)
        );
    }

    /**
     * Release.Smart can fetch body of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchBody() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String description = "Description of the release";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("body", description)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.body(),
            Matchers.equalTo(description)
        );
    }

    /**
     * Release.Smart can fetch created date of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchDescription() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String created = "2013-02-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("created_at", created)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.createdAt(),
            Matchers.equalTo(new Github.Time(created).date())
        );
    }

    /**
     * Release.Smart can fetch published date of an Release.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchPublished() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final String published = "2013-01-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("published_at", published)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            smart.publishedAt(),
            Matchers.equalTo(new Github.Time(published).date())
        );
    }

    /**
     * Release.Smart can tell when the release is a prerelease.
     * @throws Exception If problem inside
     */
    @Test
    public void isPrerelease() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("prerelease", Boolean.TRUE).build()
        ).when(release).json();
        MatcherAssert.assertThat(
            new Release.Smart(release).prerelease(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Release.Smart can tell when the release is not a prerelease.
     * @throws Exception If problem inside
     */
    @Test
    public void isNotPrerelease() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("prerelease", "false").build()
        ).when(release).json();
        MatcherAssert.assertThat(
            new Release.Smart(release).prerelease(),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * Release.Smart counts prerelease as false if its missing.
     * @throws Exception If problem inside
     */
    @Test
    public void missingPrerelease() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().build()
        ).when(release).json();
        MatcherAssert.assertThat(
            new Release.Smart(release).prerelease(),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * Release.Smart can tell when the release is a draft.
     * @throws Exception If problem inside
     */
    @Test
    public void isDraft() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("draft", Boolean.TRUE).build()
        ).when(release).json();
        MatcherAssert.assertThat(
            new Release.Smart(release).draft(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Release.Smart can tell when the release is not a draft.
     * @throws Exception If problem inside
     */
    @Test
    public void isNotDraft() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("draft", Boolean.FALSE).build()
        ).when(release).json();
        MatcherAssert.assertThat(
            new Release.Smart(release).draft(),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * Release.Smart counts draft as false if its missing.
     * @throws Exception If problem inside
     */
    @Test
    public void missingDraft() throws Exception {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().build()
        ).when(release).json();
        MatcherAssert.assertThat(
            new Release.Smart(release).draft(),
            Matchers.is(Boolean.FALSE)
        );
    }
}
