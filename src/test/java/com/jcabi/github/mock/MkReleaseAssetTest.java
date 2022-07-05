/**
 * Copyright (c) 2013-2022, jcabi.com
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
import javax.json.Json;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkReleaseAsset}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkReleaseAssetTest {

    /**
     * MkReleaseAsset can fetch its own Release.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesRelease() throws Exception {
        final Release rel = release();
        MatcherAssert.assertThat(
            rel.assets().get(1).release(),
            Matchers.is(rel)
        );
    }

    /**
     * MkReleaseAsset can fetch its own number.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesNumber() throws Exception {
        final Release rel = release();
        MatcherAssert.assertThat(
            rel.assets().get(1).number(),
            Matchers.is(1)
        );
    }

    /**
     * MkReleaseAsset can be removed.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void removesAsset() throws Exception {
        final ReleaseAssets assets = release().assets();
        final ReleaseAsset asset = assets.upload(
            "testRemove".getBytes(), "text/plain", "remove.txt"
        );
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.<ReleaseAsset>iterableWithSize(1)
        );
        asset.remove();
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleaseAsset can be removed several times.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void removesSeveralAssets() throws Exception {
        final ReleaseAssets assets = release().assets();
        // @checkstyle MagicNumberCheck (1 line)
        final int limit = 3;
        final ReleaseAsset[] bodies = new ReleaseAsset[limit];
        for (int idx = 0; idx < limit; ++idx) {
            bodies[idx] = assets.upload(
                "testRemove".getBytes(), "text/plain", "remove.txt"
            );
        }
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.<ReleaseAsset>iterableWithSize(limit)
        );
        for (int idx = 0; idx < limit; ++idx) {
            bodies[idx].remove();
        }
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleaseAsset can be represented in JSON format.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canRepresentAsJson() throws Exception {
        final String name = "json.txt";
        final String type = "text/plain";
        final ReleaseAsset asset = release().assets().upload(
            "testJson".getBytes(), type, name
        );
        MatcherAssert.assertThat(
            asset.json().getString("content_type"),
            Matchers.is(type)
        );
        MatcherAssert.assertThat(
            asset.json().getString("name"),
            Matchers.is(name)
        );
    }

    /**
     * MkReleaseAsset can patch its JSON representation.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canPatchJson() throws Exception {
        final String orig = "orig.txt";
        final ReleaseAsset asset = release().assets().upload(
            "testPatch".getBytes(), "text/plain", orig
        );
        final String attribute = "name";
        MatcherAssert.assertThat(
            asset.json().getString(attribute),
            Matchers.is(orig)
        );
        final String patched = "patched.txt";
        asset.patch(
            Json.createObjectBuilder().add(attribute, patched).build()
        );
        MatcherAssert.assertThat(
            asset.json().getString(attribute),
            Matchers.is(patched)
        );
    }

    /**
     * Should return the Base64-encoded value of the input contents. When
     * decoded, should be equal to the input.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void fetchesRawRepresentation() throws Exception {
        final String test = "This is a test asset.";
        final ReleaseAsset asset = new MkGithub().randomRepo().releases()
            .create("v1.0")
            .assets()
            .upload(test.getBytes(), "type", "name");
        MatcherAssert.assertThat(
            new String(
              DatatypeConverter.parseBase64Binary(IOUtils.toString(asset.raw()))
            ),
            Matchers.is(test)
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
