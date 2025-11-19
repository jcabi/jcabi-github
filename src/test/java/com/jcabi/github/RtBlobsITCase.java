/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Test case for {@link RtBlobs}.
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtBlobsITCase {

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
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    private static RepoRule rule = new RepoRule();

    /**
     * Set up test fixtures.
     */
    @BeforeAll
    public static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtBlobsITCase.repos = github.repos();
        RtBlobsITCase.repo = RtBlobsITCase.rule.repo(RtBlobsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    public static void tearDown() throws IOException {
        if (RtBlobsITCase.repos != null && RtBlobsITCase.repo != null) {
            RtBlobsITCase.repos.remove(RtBlobsITCase.repo.coordinates());
        }
    }

    @Test
    public void createsBlob() throws IOException {
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
    public void getsBlob() throws IOException {
        final Blobs blobs = RtBlobsITCase.repo.git().blobs();
        final String content = "Content of the blob";
        final String encoding = "base64";
        final Blob blob = blobs.create(
            content, encoding
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            blobs.get(blob.sha()).json().getString("sha"),
            Matchers.equalTo(blob.sha())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            blobs.get(blob.sha()).json().getString("encoding"),
            Matchers.equalTo(encoding)
        );
    }
}
