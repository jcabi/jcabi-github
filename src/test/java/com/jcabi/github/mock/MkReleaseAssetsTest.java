/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Release;
import com.jcabi.github.ReleaseAsset;
import com.jcabi.github.ReleaseAssets;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkReleaseAssets}.
 *
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
            IOUtils.toString(asset.raw(), StandardCharsets.UTF_8),
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
