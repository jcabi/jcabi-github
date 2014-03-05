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

import com.jcabi.aspects.Tv;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration test for {@link RtReleaseAssets}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 * @todo #509:30m The returnsNoAssets() and iteratesAssets() methods are ignored
 *  because {@link RtReleaseAssets#iterate()} is not yet implemented. When this
 *  is implemented, remove the ignore annotation and revise the tests as needed.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtReleaseAssetsITCase {

    /**
     * RtReleaseAssets can upload release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void uploadsAssets() throws Exception {
        final Releases releases = releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphabetic(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final String name = "upload.txt";
            final ReleaseAsset uploaded = assets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can fetch release assets by asset ID.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void fetchesAssets() throws Exception {
        final Releases releases = releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphabetic(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final ReleaseAsset uploaded = assets.upload(
                "fetch".getBytes(),
                "text/plain",
                "fetch.txt"
            );
            MatcherAssert.assertThat(
                assets.get(uploaded.number()),
                Matchers.is(uploaded)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can iterate through multiple release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    @Ignore
    public void iteratesAssets() throws Exception {
        final Releases releases = releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphabetic(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final ReleaseAsset first = assets.upload(
                "first".getBytes(),
                "text/plain",
                "first.txt"
            );
            final ReleaseAsset second = assets.upload(
                "second".getBytes(),
                "text/plain",
                "second.txt"
            );
            MatcherAssert.assertThat(
                assets.iterate(),
                Matchers.contains(first, second)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can return an empty list of release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    @Ignore
    public void returnsNoAssets() throws Exception {
        final Releases releases = releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphabetic(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            MatcherAssert.assertThat(
                assets.iterate(),
                Matchers.emptyIterable()
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * Create and return Release object to test.
     * @return Releases
     * @throws IOException If an IO Exception occurs.
     */
    private static Releases releases() throws IOException {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).repos().get(
            new Coordinates.Simple(System.getProperty("failsafe.github.repo"))
        ).releases();
    }

}
