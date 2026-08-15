/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Release;
import com.jcabi.github.ReleaseAsset;
import com.jcabi.github.ReleaseAssets;
import jakarta.json.Json;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkReleaseAsset}.
 * @since 0.8
 */
final class MkReleaseAssetTest {

    /**
     * Name of the JSON attribute with the name of an asset.
     */
    private static final String NAME = "name";

    /**
     * Amount of assets to upload.
     */
    private static final int LIMIT = 3;

    /**
     * MkReleaseAsset can fetch its own Release.
     * @throws Exception If a problem occurs.
     */
    @Test
    void fetchesRelease() throws Exception {
        final Release rel = MkReleaseAssetTest.release();
        MatcherAssert.assertThat(
            "Values are not equal",
            rel.assets().get(1).release(),
            Matchers.is(rel)
        );
    }

    /**
     * MkReleaseAsset can fetch its own number.
     * @throws Exception If a problem occurs.
     */
    @Test
    void fetchesNumber() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            MkReleaseAssetTest.release().assets().get(1).number(),
            Matchers.is(1)
        );
    }

    /**
     * MkReleaseAsset can be removed.
     * @throws Exception If a problem occurs.
     */
    @Test
    void uploadsAsset() throws Exception {
        final ReleaseAssets assets = MkReleaseAssetTest.release().assets();
        MkReleaseAssetTest.uploaded(assets);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            assets.iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkReleaseAsset can be removed.
     * @throws Exception If a problem occurs.
     */
    @Test
    void removesAsset() throws Exception {
        final ReleaseAssets assets = MkReleaseAssetTest.release().assets();
        MkReleaseAssetTest.uploaded(assets).remove();
        MatcherAssert.assertThat(
            "Collection is not empty",
            assets.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleaseAsset can be uploaded several times.
     * @throws Exception If a problem occurs.
     */
    @Test
    void uploadsSeveralAssets() throws Exception {
        final ReleaseAssets assets = MkReleaseAssetTest.release().assets();
        for (int idx = 0; idx < MkReleaseAssetTest.LIMIT; ++idx) {
            MkReleaseAssetTest.uploaded(assets);
        }
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            assets.iterate(),
            Matchers.iterableWithSize(MkReleaseAssetTest.LIMIT)
        );
    }

    /**
     * MkReleaseAsset can be removed several times.
     * @throws Exception If a problem occurs.
     */
    @Test
    void removesSeveralAssets() throws Exception {
        final ReleaseAssets assets = MkReleaseAssetTest.release().assets();
        final ReleaseAsset[] bodies =
            new ReleaseAsset[MkReleaseAssetTest.LIMIT];
        for (int idx = 0; idx < MkReleaseAssetTest.LIMIT; ++idx) {
            bodies[idx] = MkReleaseAssetTest.uploaded(assets);
        }
        for (final ReleaseAsset body : bodies) {
            body.remove();
        }
        MatcherAssert.assertThat(
            "Collection is not empty",
            assets.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleaseAsset can be represented in JSON format.
     * @throws Exception If a problem occurs.
     */
    @Test
    void canRepresentAsJson() throws Exception {
        MatcherAssert.assertThat(
            "Asset has a wrong content type",
            MkReleaseAssetTest.uploaded(
                MkReleaseAssetTest.release().assets()
            ).json().getString("content_type"),
            Matchers.is("text/plain")
        );
    }

    /**
     * MkReleaseAsset can show its own name in JSON format.
     * @throws Exception If a problem occurs.
     */
    @Test
    void showsNameInJson() throws Exception {
        MatcherAssert.assertThat(
            "Asset has a wrong name",
            MkReleaseAssetTest.uploaded(
                MkReleaseAssetTest.release().assets()
            ).json().getString(MkReleaseAssetTest.NAME),
            Matchers.is("remove.txt")
        );
    }

    /**
     * MkReleaseAsset can patch its JSON representation.
     * @throws Exception If a problem occurs.
     */
    @Test
    void canPatchJson() throws Exception {
        final ReleaseAsset asset = MkReleaseAssetTest.uploaded(
            MkReleaseAssetTest.release().assets()
        );
        final String patched = "patched.txt";
        asset.patch(
            Json.createObjectBuilder()
                .add(MkReleaseAssetTest.NAME, patched)
                .build()
        );
        MatcherAssert.assertThat(
            "Asset is not patched",
            asset.json().getString(MkReleaseAssetTest.NAME),
            Matchers.is(patched)
        );
    }

    /**
     * Should return the Base64-encoded value of the input contents. When
     * decoded, should be equal to the input.
     */
    @Test
    void fetchesRawRepresentation() throws IOException {
        final String test = "This is a test asset.";
        MatcherAssert.assertThat(
            "Values are not equal",
            new String(
                DatatypeConverter.parseBase64Binary(
                    IOUtils.toString(
                        new MkGitHub().randomRepo().releases()
                            .create("v1.0")
                            .assets()
                            .upload(test.getBytes(StandardCharsets.UTF_8), "type", "name").raw(),
                        StandardCharsets.UTF_8
                    )
                ),
                StandardCharsets.UTF_8
            ),
            Matchers.is(test)
        );
    }

    /**
     * Create a Release to work with.
     * @return Repo
     * @throws IOException If a problem occurs.
     */
    private static Release release() throws IOException {
        return new MkGitHub().randomRepo().releases().create("v1.0");
    }

    /**
     * Upload an asset to the given collection.
     * @param assets Collection to upload to
     * @return Uploaded asset
     * @throws IOException If a problem occurs.
     */
    private static ReleaseAsset uploaded(final ReleaseAssets assets)
        throws IOException {
        return assets.upload(
            "testRemove".getBytes(StandardCharsets.UTF_8),
            "text/plain",
            "remove.txt"
        );
    }
}
