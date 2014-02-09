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

import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleaseAssets}.
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReleaseAssetsTest {

    /**
     * RtReleaseAssets can fetch empty list of release assets.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfReleaseAssets() throws Exception {
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest().withBody("[]"),
            RtReleaseAssetsTest.repo()
        );
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * RtReleaseAssets can fetch non empty list of release assets.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canFetchNonEmptyListOfReleaseAssets()  throws Exception {
        final int number = 1;
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest().withBody(
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("id", number)
                        .add("tag_name", "v1.0.0")
                        .add("name", "v1.0.0")
                        .add("body", "Asset")
                ).build().toString()
            ),
            RtReleaseAssetsTest.repo()
        );
        MatcherAssert.assertThat(
            assets.iterate().iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * RtReleaseAssets can fetch a single release asset.
     * @throws java.io.IOException If some problem inside
     */
    @Test
    public void canFetchSingleReleaseAsset() throws IOException {
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest(), RtReleaseAssetsTest.repo()
        );
        MatcherAssert.assertThat(assets.get(1), Matchers.notNullValue());
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "assets"))
            .when(repo).coordinates();
        return repo;
    }

}
