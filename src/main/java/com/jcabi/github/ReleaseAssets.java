/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub release assets.
 * @see <a href="https://developer.github.com/v3/repos/releases/">Releases API</a>
 * @since 0.8
 */
@Immutable
public interface ReleaseAssets {

    /**
     * The release we're in.
     * @return Issue
     */
    Release release();

    /**
     * Iterate them all.
     * @return All comments
     * @see <a href="https://developer.github.com/v3/repos/releases/#list-assets-for-a-release">List Assets for a Release</a>
     */
    Iterable<ReleaseAsset> iterate();

    /**
     * Upload a release asset.
     * @param content The raw content bytes.
     * @param type Content-Type of the release asset.
     * @param name Name of the release asset.
     * @return The new release asset.
     * @throws IOException If an IO Exception occurs
     * @see <a href="https://developer.github.com/v3/repos/releases/#upload-a-release-asset">Upload a Release Asset</a>
     */
    ReleaseAsset upload(
        byte[] content,
        String type,
        String name
    ) throws IOException;

    /**
     * Get a single release asset.
     * @param number The release asset ID.
     * @return The release asset.
     * @see <a href="https://developer.github.com/v3/repos/releases/#get-a-single-release-asset">Get a Single Release Asset</a>
     */
    ReleaseAsset get(int number);

}
