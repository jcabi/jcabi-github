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

import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.request.ApacheRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtRelease}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public class RtReleaseTest {

    /**
     * An empty JSON string.
     */
    private static final String EMPTY_JSON = "{}";

    /**
     * A test mnemo.
     */
    private static final String TEST_MNEMO = "tstuser/tstbranch";

    /**
     * RtRelease can edit a release.
     * @todo #180 RtRelease should be able to edit a release. Let's implement
     *  this method, add integration test, declare a method in Release and
     *  implement it. See
     *  http://developer.github.com/v3/repos/releases/#edit-a-release. When
     *  done, remove this puzzle and Ignore annotation from this method.
     */
    @Test
    @Ignore
    public void editRelease() {
        // to be implemented
    }

    /**
     * RtRelease can delete a release.
     * @throws Exception If any problems in the test occur.
     */
    @Test
    public final void deleteRelease() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, EMPTY_JSON)
        ).start();
        final RtRelease release = new RtRelease(
            new ApacheRequest(container.home()),
            new Coordinates.Simple(TEST_MNEMO),
            2
        );
        release.delete();
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.DELETE)
        );
        container.stop();
    }

    /**
     * RtRelease can list assets for a release.
     * @checkstyle LineLength (4 lines)
     * @todo #180 RtRelease should be able to list assets for a release. Let's
     *  implement this method, add integration test, declare a method in
     *  Release and implement it. See
     *  http://developer.github.com/v3/repos/releases/#list-assets-for-a-release.
     *  When done, remove this puzzle and Ignore annotation from this method.
     */
    @Test
    @Ignore
    public void listReleaseAssets() {
        // to be implemented
    }

    /**
     * RtRelease can upload a release asset.
     * @todo #180 RtRelease should be able to upload a release asset. Let's
     *  implement this method, add integration test, declare a method in
     *  Release and implement it. See
     *  http://developer.github.com/v3/repos/releases/#upload-a-release-asset.
     *  When done, remove this puzzle and Ignore annotation from this method.
     */
    @Test
    @Ignore
    public void uploadReleaseAsset() {
        // to be implemented
    }

    /**
     * RtRelease can get a single release asset.
     * @checkstyle LineLength (4 lines)
     * @todo #180 RtRelease should be able to get a single release asset. Let's
     *  implement this method, add integration test, declare a method in
     *  Release and implement it. See
     *  http://developer.github.com/v3/repos/releases/#get-a-single-release-asset.
     *  When done, remove this puzzle and Ignore annotation from this method.
     */
    @Test
    @Ignore
    public void getReleaseAsset() {
        // to be implemented
    }

    /**
     * RtRelese can execute PATCH request.
     * @throws Exception if there is any problem
     */
    @Test
    public final void executePatchRequest() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, EMPTY_JSON)
        ).start();
        final RtRelease release = new RtRelease(
            new ApacheRequest(container.home()),
            new Coordinates.Simple(TEST_MNEMO), 2
        );
        release.patch(Json.createObjectBuilder().add("name", "v1")
            .build()
        );
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.PATCH)
        );
        container.stop();
    }

}
