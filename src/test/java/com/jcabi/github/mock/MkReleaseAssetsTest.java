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

import com.jcabi.github.Release;
import com.jcabi.github.ReleaseAsset;
import com.jcabi.github.ReleaseAssets;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkReleaseAssets}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 * @checkstyle MethodNameCheck (200 lines)
 */
public final class MkReleaseAssetsTest {

    /**
     * MkReleaseAssets can upload a new Release Asset.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void uploadsNewAsset() throws Exception {
        final ReleaseAssets assets = release().assets();
        final ReleaseAsset asset = assets.upload(
            "testUpload".getBytes(), "text/plain", "upload.txt"
        );
        MatcherAssert.assertThat(
            asset.number(),
            Matchers.is(1)
        );
    }

    /**
     * MkReleaseAssets can fetch a single Release Asset.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesSingleAsset() throws Exception {
        final ReleaseAssets assets = release().assets();
        final ReleaseAsset asset = assets.upload(
            "testGet".getBytes(), "text/plain", "get.txt"
        );
        MatcherAssert.assertThat(
            assets.get(asset.number()),
            Matchers.is(asset)
        );
    }

    /**
     * MkReleaseAssets can iterate through Release Assets.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void iteratesAssets() throws Exception {
        final ReleaseAssets assets = release().assets();
        assets.upload(
            "testIterate".getBytes(), "text/plain", "iterate.txt"
        );
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkReleaseAssets can fetch its own Release.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesRelease() throws Exception {
        final Release rel = release();
        MatcherAssert.assertThat(
            rel.assets().release(),
            Matchers.is(rel)
        );
    }

    /**
     * Must encode the input bytes into Base64.
     * @throws Exception Unexpected.
     */
    @Test
    public void encodesContentsAsBase64() throws Exception {
        final String test = "This is a test asset.";
        final ReleaseAsset asset = new MkGithub().randomRepo().releases()
            .create("v1.0")
            .assets()
            .upload(test.getBytes(), "type", "name");
        MatcherAssert.assertThat(
            IOUtils.toString(asset.raw()),
            Matchers.is(DatatypeConverter.printBase64Binary(test.getBytes()))
        );
    }

    /**
     * Create a Release to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Release release() throws Exception {
        return new MkGithub().randomRepo().releases().create("v1.0");
    }
}
