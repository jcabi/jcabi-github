/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.InputStream;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleaseAsset}.
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
public final class RtReleaseAssetTest {

    /**
     * RtReleaseAsset can be described in JSON form.
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canRepresentAsJson() throws Exception {
        final RtReleaseAsset asset = new RtReleaseAsset(
            new FakeRequest().withBody("{\"asset\":\"release\"}"),
            release(),
            1
        );
        MatcherAssert.assertThat(
            asset.json().getString("asset"),
            Matchers.equalTo("release")
        );
    }

    /**
     * RtReleaseAsset can obtain its own release.
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canObtainOwnRelease() throws Exception {
        final Release release = release();
        final RtReleaseAsset asset = new RtReleaseAsset(
            new FakeRequest(),
            release,
            1
        );
        MatcherAssert.assertThat(
            asset.release(),
            Matchers.is(release)
        );
    }

    /**
     * RtReleaseAsset can create a patch request.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void patchesAsset() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
        ).start();
        final RtReleaseAsset asset = new RtReleaseAsset(
            new ApacheRequest(container.home()),
            release(),
            2
        );
        try {
            final JsonObject json = Json.createObjectBuilder()
                .add("name", "hello").build();
            asset.patch(json);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(), Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.containsString("{\"name\":\"hello\"}")
            );
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/repos/john/test/releases/assets/2")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReleaseAsset can remove itself.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void removesAsset() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start();
        final RtReleaseAsset asset = new RtReleaseAsset(
            new ApacheRequest(container.home()),
            release(),
            3
        );
        try {
            asset.remove();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReleaseAsset can stream raw content.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void rawAsset() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
        ).start();
        final RtReleaseAsset asset = new RtReleaseAsset(
            new ApacheRequest(container.home()),
            release(),
            4
        );
        try {
            final InputStream stream = asset.raw();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(), Matchers.equalTo(Request.GET)
            );
            MatcherAssert.assertThat(
                IOUtils.toString(stream),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * This method returns a Release for testing.
     * @return Release to be used for test.
     * @throws Exception - if anything goes wrong.
     */
    private static Release release() throws Exception {
        final Release release = Mockito.mock(Release.class);
        final Repo repo = new MkGithub("john").repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
        Mockito.doReturn(repo).when(release).repo();
        Mockito.doReturn(1).when(release).number();
        return release;
    }
}
