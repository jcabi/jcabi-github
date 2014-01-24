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

import com.rexsl.test.request.FakeRequest;
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
     * @todo #180 RtRelease should be able to delete a release. Let's implement
     *  this method, add integration test, declare a method in Release
     *  and implement it. See
     *  http://developer.github.com/v3/repos/releases/#delete-a-release. When
     *  done, remove this puzzle and Ignore annotation from this method.
     */
    @Test
    @Ignore
    public void deleteRelease() {
        // to be implemented
    }

    /**
     * RtRelease can list assets for a release.
     * See
     * http://developer.github.com/v3/repos/releases/#list-assets-for-a-release
     * @throws java.io.IOException if io error occurs
     */
    @Test
    public final void listReleaseAssets() throws java.io.IOException {
        final FakeRequest req = new FakeRequest();
        final Coordinates.Simple coords = new Coordinates.Simple("test",
            "releases");
        final Release release = new RtRelease(
            req, coords, 1
        );
        MatcherAssert.assertThat(
            release.iterate(
                req, coords
            ),
            Matchers.notNullValue()
        );
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

}
