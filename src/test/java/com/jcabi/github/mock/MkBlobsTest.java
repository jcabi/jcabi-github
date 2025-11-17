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
import org.junit.Test;

/**
 * Test case for {@link MkBlobs).
 */
public final class MkBlobsTest {

    /**
     * MkBlobs should be able to create a blob.
     *
     */
    @Test
    public void canCreateBlob() throws IOException {
        final Blobs blobs = new MkGithub().randomRepo().git().blobs();
        final Blob blob = blobs.create("content1", "encoding1");
        MatcherAssert.assertThat(
            blobs.get(blob.sha()),
            Matchers.equalTo(blob)
        );
    }

    /**
     * MkBlobs can get a blob.
     */
    @Test
    public void getBlob() throws IOException {
        final Blobs blobs = new MkGithub().randomRepo().git().blobs();
        final Blob created =  blobs.create("content", "base64");
        MatcherAssert.assertThat(
            blobs.get(created.sha()),
            Matchers.notNullValue()
        );
    }
}
