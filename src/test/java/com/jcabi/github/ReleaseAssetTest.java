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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link ReleaseAsset}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (150 lines)
 */
public final class ReleaseAssetTest {

    /**
     * ReleaseAsset.Smart can fetch key properties of a ReleaseAsset.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesProperties() throws Exception {

        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", "https://api.github.com/repos/octocat/Hello-World/releases/assets/1")
                .add("name", "assetname.ext")
                .add("label", "short description")
                .add("state", "uploaded")
                .add("content_type", "application/zip")
                .add("size", 1024)
                .add("download_count", 42)
                .add("created_at", "2013-02-27T19:35:32Z")
                .add("updated_at", "2013-02-27T19:35:32Z")
                .build()
        ).when(releaseAsset).json();

        final ReleaseAsset.Smart smart = new ReleaseAsset.Smart(releaseAsset);
        MatcherAssert.assertThat(
            smart.url(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.name(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.label(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.state(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.contentType(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.size(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.downloadCount(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.createdAt(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.updatedAt(),
            Matchers.notNullValue()
        );
    }
}
