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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration case for {@link Milestones}.
 * @since 0.33.1
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtIssueMilestoneITCase {
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
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = new RepoRule().repo(RtIssueMilestoneITCase.repos);
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

    @Test
    public void addIssueToMilestone() throws Exception {
        final Issue issue = RtIssueMilestoneITCase.issue();
        final Milestone milestone = RtIssueMilestoneITCase.repo
            .milestones().create("one");
        new Issue.Smart(issue).milestone(milestone);
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).openIssues(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    public void checkMilestone() throws Exception {
        final Issue.Smart issue = new Issue.Smart(
            RtIssueMilestoneITCase.issue()
        );
        MatcherAssert.assertThat(
            issue.hasMilestone(),
            Matchers.is(false)
        );
        final Milestone milestone = RtIssueMilestoneITCase.repo
            .milestones().create("two");
        issue.milestone(milestone);
        MatcherAssert.assertThat(
            issue.hasMilestone(),
            Matchers.is(true)
        );
    }

    @Test
    public void readMilestone() throws Exception {
        final String title = "three";
        final Issue.Smart issue = new Issue.Smart(
            RtIssueMilestoneITCase.issue()
        );
        issue.milestone(
            RtIssueMilestoneITCase.repo.milestones().create(title)
        );
        MatcherAssert.assertThat(
            new Milestone.Smart(
                new Issue.Smart(
                    RtIssueMilestoneITCase.repo.issues().get(issue.number())
                ).milestone()
            ).title(),
            Matchers.equalTo(title)
        );
    }

    /**
     * Create and return issue to test.
     * @return Issue
     * @throws Exception If some problem inside
     */
    private static Issue issue() throws Exception {
        return repo.issues().create("test issue title", "test issue body");
    }
}
