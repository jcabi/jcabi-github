/**
 * Copyright (c) 2013-2020, jcabi.com
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

import com.jcabi.github.OAuthScope.Scope;
import com.jcabi.immutable.ArrayMap;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link Github}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@OAuthScope(Scope.REPO)
final class RtIssuesITCase {
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
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
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
     * RtIssues can iterate issues.
     * @throws Exception If some problem inside
     */
    @Test
    void iteratesIssues() throws Exception {
        final Iterable<Issue.Smart> issues = new Smarts<Issue.Smart>(
            new Bulk<Issue>(
                repo.issues().iterate(
                    new ArrayMap<String, String>().with("sort", "comments")
                )
            )
        );
        for (final Issue.Smart issue : issues) {
            MatcherAssert.assertThat(
                issue.title(),
                Matchers.notNullValue()
            );
        }
    }

    /**
     * RtIssues can search issues within a repository.
     * @throws Exception If some problem inside
     */
    @Test
    void searchesIssues() throws Exception {
        final String targetLabel = "bug";
        final EnumMap<Issues.Qualifier, String> qualifiers =
            new EnumMap<Issues.Qualifier, String>(Issues.Qualifier.class);
        qualifiers.put(Issues.Qualifier.LABELS, targetLabel);
        final Iterable<Issue.Smart> issues = new Smarts<Issue.Smart>(
            new Bulk<Issue>(
                repo.issues().search(
                    Issues.Sort.UPDATED,
                    Search.Order.ASC,
                    qualifiers
                )
            )
        );
        Date prevUpdated = null;
        final Set<String> labelNames = new HashSet<String>();
        for (final Issue.Smart issue : issues) {
            MatcherAssert.assertThat(
                issue.title(),
                Matchers.notNullValue()
            );
            if (prevUpdated != null) {
                MatcherAssert.assertThat(
                    issue.updatedAt(),
                    Matchers.lessThanOrEqualTo(prevUpdated)
                );
            }
            prevUpdated = issue.updatedAt();
            labelNames.clear();
            for (final Label label : issue.roLabels().iterate()) {
                labelNames.add(label.name());
            }
            MatcherAssert.assertThat(
                labelNames,
                Matchers.contains(targetLabel)
            );
        }
    }
}
