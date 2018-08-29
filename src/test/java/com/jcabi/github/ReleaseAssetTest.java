/**
 * Copyright (c) 2013-2018, jcabi.com
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

import java.net.URL;
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
@SuppressWarnings("PMD.TooManyMethods")
public class ReleaseAssetTest {

    /**
     * ReleaseAsset.Smart can fetch url property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesUrl() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        // @checkstyle LineLength (1 line)
        final String prop = "https://api.github.com/repos/octo/Hello/releases/assets/1";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).url(),
            Matchers.is(new URL(prop))
        );
    }

    /**
     * ReleaseAsset.Smart can fetch name property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesName() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "assetname.ext";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).name(),
            Matchers.is(prop)
        );
    }

    /**
     * ReleaseAsset.Smart can fetch label property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesLabel() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "short description";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("label", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).label(),
            Matchers.is(prop)
        );
    }

    /**
     * ReleaseAsset.Smart can fetch state property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesState() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "uploaded";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("state", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).state(),
            Matchers.is(prop)
        );
    }

    /**
     * ReleaseAsset.Smart can fetch content_type property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesContentType() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "application/zip";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("content_type", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).contentType(),
            Matchers.is(prop)
        );
    }

    /**
     * ReleaseAsset.Smart can fetch size property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesSize() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final int prop = 1024;
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("size", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).size(),
            Matchers.is(prop)
        );
    }

    /**
     * ReleaseAsset.Smart can fetch download_count property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesDownloadCount() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final int prop = 42;
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("download_count", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).downloadCount(),
            Matchers.is(prop)
        );
    }

    /**
     * ReleaseAsset.Smart can fetch created_at property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesCreatedAt() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "2013-02-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("created_at", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).createdAt(),
            Matchers.equalTo(new Github.Time(prop).date())
        );
    }

    /**
     * ReleaseAsset.Smart can fetch updated_at property from ReleaseAsset.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesUpdatedAt() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "2013-02-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("updated_at", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            new ReleaseAsset.Smart(releaseAsset).updatedAt(),
            Matchers.equalTo(new Github.Time(prop).date())
        );
    }

    /**
     * ReleaseAsset.Smart can update the name property of ReleaseAsset.
     * @throws Exception If a problem occurs.
     */
    @Test
    public final void updatesName() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "new_name";
        new ReleaseAsset.Smart(releaseAsset).name(prop);
        Mockito.verify(releaseAsset).patch(
            Json.createObjectBuilder().add("name", prop).build()
        );
    }

    /**
     * ReleaseAsset.Smart can update the label property of ReleaseAsset.
     * @throws Exception If a problem occurs.
     */
    @Test
    public final void updatesLabel() throws Exception {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "new_label";
        new ReleaseAsset.Smart(releaseAsset).label(prop);
        Mockito.verify(releaseAsset).patch(
            Json.createObjectBuilder().add("label", prop).build()
        );
    }
}
