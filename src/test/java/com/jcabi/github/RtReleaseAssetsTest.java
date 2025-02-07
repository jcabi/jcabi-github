/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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
import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.request.FakeRequest;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleaseAssets}.
 * @since 0.8
 */
public final class RtReleaseAssetsTest {

    /**
     * RtRelease can list assets for a release.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void listReleaseAssets() throws Exception {
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest().withStatus(HttpURLConnection.HTTP_OK)
                .withBody("[{\"id\":1},{\"id\":2}]"), release()
        );
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.<ReleaseAsset>iterableWithSize(2)
        );
    }

    /**
     * RtRelease can upload a release asset.
     *
     * @throws Exception If something goes wrong
     */
    @Test
    public void uploadReleaseAsset() throws Exception {
        final String body = "{\"id\":1}";
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest().withStatus(HttpURLConnection.HTTP_CREATED)
                .withBody(body),
            release()
        );
        MatcherAssert.assertThat(
            assets.upload(body.getBytes(), "text/plain", "hello.txt")
                .number(),
            Matchers.is(1)
        );
    }

    /**
     * RtRelease can get a single release asset.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void getReleaseAsset() throws Exception {
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest().withStatus(HttpURLConnection.HTTP_OK)
                .withBody("{\"id\":3}"),
                release()
        );
        MatcherAssert.assertThat(
            assets.get(Tv.THREE).number(),
            Matchers.is(Tv.THREE)
        );
    }

    /**
     * This method returns a Release for testing.
     * @return Release to be used for test.
     * @throws Exception - if anything goes wrong.
     */
    private static Release release() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final Repo repo = new MkGithub("john").randomRepo();
        Mockito.doReturn(repo).when(release).repo();
        Mockito.doReturn(1).when(release).number();
        return release;
    }
}
