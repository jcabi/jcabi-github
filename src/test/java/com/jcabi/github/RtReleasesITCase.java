/**
 * Copyright (c) 2013-2017, jcabi.com
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
import com.jcabi.github.OAuthScope.Scope;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtReleases}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
@OAuthScope(Scope.REPO)
public final class RtReleasesITCase {

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
        final String key = SystemProperty.githubKey();
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
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
     * RtReleases can iterate releases.
     * @throws Exception if something goes wrong
     */
    @Test
    public void canFetchAllReleases() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                releases.iterate(),
                Matchers.not(Matchers.emptyIterableOf(Release.class))
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleases can fetch a single release.
     * @throws Exception if any error inside
     */
    @Test
    public void canFetchRelease() throws Exception {
        final Releases releases = repo.releases();
        final String tag = "v1.0";
        final Release release = releases.create(tag);
        MatcherAssert.assertThat(
            releases.get(release.number()).number(),
            Matchers.equalTo(release.number())
        );
        MatcherAssert.assertThat(
            new Release.Smart(releases.get(release.number())).tag(),
            Matchers.equalTo(tag)
        );
        releases.remove(release.number());
    }

    /**
     * RtReleases can create a release.
     * @throws Exception if any error inside
     */
    @Test
    public void canCreateRelease() throws Exception {
        final Releases releases = repo.releases();
        final Release created = releases.create("0.1");
        final int number = created.number();
        try {
            final Release obtained = releases.get(number);
            MatcherAssert.assertThat(
                created,
                Matchers.is(obtained)
            );
            MatcherAssert.assertThat(
                new Release.Smart(created).tag(),
                Matchers.equalTo(new Release.Smart(obtained).tag())
            );
        } finally {
            releases.remove(number);
        }
    }

    /**
     * RtReleases can remove a release.
     * @throws Exception if any problem inside
     */
    @Test
    public void canRemoveRelease() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.hasItem(release)
        );
        releases.remove(release.number());
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.not(Matchers.hasItem(release))
        );
    }

    /**
     * RtReleases can edit release tag.
     * @throws Exception if any problem inside.
     */
    @Test
    public void canEditTag() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final String tag = RandomStringUtils.randomAlphanumeric(Tv.FIFTEEN);
        new Release.Smart(release).tag(tag);
        MatcherAssert.assertThat(
            new Release.Smart(releases.get(release.number())).tag(),
            Matchers.equalTo(tag)
        );
        releases.remove(release.number());
    }

    /**
     * RtReleases can edit release body.
     * @throws Exception if any problem inside.
     */
    @Test
    public void canEditBody() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final String body = "Description of the release";
        new Release.Smart(release).body(body);
        MatcherAssert.assertThat(
            new Release.Smart(releases.get(release.number())).body(),
            Matchers.equalTo(body)
        );
        releases.remove(release.number());
    }
}
