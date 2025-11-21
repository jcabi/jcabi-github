/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Blob;
import com.jcabi.github.Blobs;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkBlobs}.
 * @since 0.8
 */
final class MkBlobsTest {

    @Test
    void canCreateBlob() throws IOException {
        final Blobs blobs = new MkGitHub().randomRepo().git().blobs();
        final Blob blob = blobs.create("content1", "encoding1");
        MatcherAssert.assertThat(
            "Values are not equal",
            blobs.get(blob.sha()),
            Matchers.equalTo(blob)
        );
    }

    @Test
    void getBlob() throws IOException {
        final Blobs blobs = new MkGitHub().randomRepo().git().blobs();
        final Blob created =  blobs.create("content", "base64");
        MatcherAssert.assertThat(
            "Value is null",
            blobs.get(created.sha()),
            Matchers.notNullValue()
        );
    }
}
