/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtBlobs}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtBlobsITCase {

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * RepoRule.
     */
    private static RepoRule rule = new RepoRule();

    /**
     * Set up test fixtures.
     */
    @BeforeAll
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtBlobsITCase.repos = github.repos();
        RtBlobsITCase.repo = RtBlobsITCase.rule.repo(RtBlobsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtBlobsITCase.repos != null && RtBlobsITCase.repo != null) {
            RtBlobsITCase.repos.remove(RtBlobsITCase.repo.coordinates());
        }
    }

    @Test
    void createsBlob() throws IOException {
        final Blobs blobs = RtBlobsITCase.repo.git().blobs();
        final Blob blob = blobs.create(
            "Test Content", "utf-8"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            blob.sha(),
            Matchers.equalTo(blob.json().getString("sha"))
        );
    }

    @Test
    void getsBlobSha() throws IOException {
        final Blobs blobs = RtBlobsITCase.repo.git().blobs();
        final Blob blob = blobs.create("Content of the blob", "base64");
        MatcherAssert.assertThat(
            "Fetched blob has a wrong SHA",
            blobs.get(blob.sha()).json().getString("sha"),
            Matchers.equalTo(blob.sha())
        );
    }

    @Test
    void getsBlobEncoding() throws IOException {
        final Blobs blobs = RtBlobsITCase.repo.git().blobs();
        final String encoding = "base64";
        MatcherAssert.assertThat(
            "Fetched blob has a wrong encoding",
            blobs.get(
                blobs.create("Content of the blob", encoding).sha()
            ).json().getString("encoding"),
            Matchers.equalTo(encoding)
        );
    }
}
