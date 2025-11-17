/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Release;
import com.jcabi.github.ReleaseAsset;
import com.jcabi.github.ReleaseAssets;
import jakarta.json.Json;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkReleaseAsset}.
 *
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
              DatatypeConverter.parseBase64Binary(
                  IOUtils.toString(asset.raw(), StandardCharsets.UTF_8)
              )
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
