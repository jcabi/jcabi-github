/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtBlobs}.
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@OAuthScope(Scope.REPO)
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
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = rule.repo(repos);
    }

    /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtBlobs can create a blob.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createsBlob() throws Exception {
        final Blobs blobs = repo.git().blobs();
        final Blob blob = blobs.create(
            "Test Content", "utf-8"
        );
        MatcherAssert.assertThat(
            blob.sha(),
            Matchers.equalTo(blob.json().getString("sha"))
        );
    }

    /**
     * RtBlobs can get a blob.
     * @throws Exception If something goes wrong
     */
    @Test
    public void getsBlob() throws Exception {
        final Blobs blobs = repo.git().blobs();
        final String content = "Content of the blob";
        final String encoding = "base64";
        final Blob blob = blobs.create(
            content, encoding
        );
        MatcherAssert.assertThat(
            blobs.get(blob.sha()).json().getString("sha"),
            Matchers.equalTo(blob.sha())
        );
        MatcherAssert.assertThat(
            blobs.get(blob.sha()).json().getString("encoding"),
            Matchers.equalTo(encoding)
        );
    }
}
