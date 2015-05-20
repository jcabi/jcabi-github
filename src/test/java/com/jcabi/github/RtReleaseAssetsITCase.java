/**
 * Copyright (c) 2013-2015, jcabi.com
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
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link RtReleaseAssets}. This test requires OAuth scope
 * "repo".
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtReleaseAssetsITCase {

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
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
        repos = github.repos();
        repo = rule.repo(repos);
        repo.releases().create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
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
     * RtReleaseAssets can upload release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void uploadsAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final String name = "upload.txt";
            final ReleaseAsset uploaded = assets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can upload two release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void uploadsTwoAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final String name = "upload.txt";
            final ReleaseAsset uploaded = assets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
            final String othername = "upload2.txt";
            final ReleaseAsset otheruploaded = assets.upload(
                "upload2".getBytes(),
                "text/plain",
                othername
            );
            MatcherAssert.assertThat(
                otheruploaded.json().getString("name"),
                Matchers.is(othername)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can upload one release assets to two releases.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void uploadsSameAssetInTwoReleases() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final Release otherrelease = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final ReleaseAssets assets = release.assets();
        final ReleaseAssets otherassets = otherrelease.assets();
        try {
            final String name = "upload.txt";
            final ReleaseAsset uploaded = assets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
            final ReleaseAsset otheruploaded = otherassets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                otheruploaded.json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
            releases.remove(otherrelease.number());
        }
    }

    /**
     * RtReleaseAssets can fetch release assets by asset ID.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void fetchesAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final ReleaseAsset uploaded = assets.upload(
                "fetch".getBytes(),
                "text/plain",
                "fetch.txt"
            );
            MatcherAssert.assertThat(
                assets.get(uploaded.number()),
                Matchers.is(uploaded)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can iterate through multiple release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void iteratesAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final ReleaseAsset first = assets.upload(
                "first".getBytes(),
                "text/plain",
                "first.txt"
            );
            final ReleaseAsset second = assets.upload(
                "second".getBytes(),
                "text/plain",
                "second.txt"
            );
            MatcherAssert.assertThat(
                assets.iterate(),
                Matchers.contains(first, second)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can return an empty list of release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void returnsNoAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            MatcherAssert.assertThat(
                assets.iterate(),
                Matchers.emptyIterable()
            );
        } finally {
            releases.remove(release.number());
        }
    }

}
