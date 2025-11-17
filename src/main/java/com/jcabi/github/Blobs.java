/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Github Git Data Blobs.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/git/blobs/">Blobs API</a>
 */
@Immutable
public interface Blobs {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Get specific blob by sha.
     * @param sha SHA of a blob
     * @return Blob
     * @see <a href="https://developer.github.com/v3/git/blobs/#get-a-blob">Get single blob</a>
     */
    Blob get(String sha);

    /**
     * Create a blob.
     * @param content Content
     * @param encoding Encoding
     * @return A new blob
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/git/blobs/#create-a-blob">Create a Blob</a>
     */
    Blob create(String content, String encoding) throws IOException;

}
