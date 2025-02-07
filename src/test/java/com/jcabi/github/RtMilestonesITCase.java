/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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
import com.jcabi.http.wire.RetryWire;
import com.jcabi.immutable.ArrayMap;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration case for {@link Milestones}.
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtMilestonesITCase {
    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new RtGithub(
            new GithubIT().connect().entry().through(RetryWire.class)
        );
        repos = github.repos();
        repo = new RepoRule().repo(repos);
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
     * RtMilestones can iterate milestones.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesIssues() throws Exception {
        final Milestones milestones = repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.randomAlphabetic(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                milestones.iterate(Collections.singletonMap("state", "all")),
                Matchers.hasItem(milestone)
            );
        } finally {
            milestones.remove(milestone.number());
        }
    }

    /**
     * RtMilestones can create a new milestone.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsNewMilestone() throws Exception {
        final Milestones milestones = repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.randomAlphabetic(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                milestones.iterate(Collections.singletonMap("state", "all")),
                Matchers.not(Matchers.emptyIterable())
            );
        } finally {
            milestones.remove(milestone.number());
        }
    }

    /**
     * RtMilestones can remove a milestone.
     * @throws Exception if some problem inside
     */
    @Test
    public void deleteMilestone() throws Exception {
        final Milestones milestones = repo.milestones();
        final Milestone milestone = milestones.create("a milestones");
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<>()),
            Matchers.hasItem(milestone)
        );
        milestones.remove(milestone.number());
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<>()),
            Matchers.not(Matchers.hasItem(milestone))
        );
    }
}
