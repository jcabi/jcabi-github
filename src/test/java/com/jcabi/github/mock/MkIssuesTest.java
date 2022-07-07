/**
 * Copyright (c) 2013-2022, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Tv;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.immutable.ArrayMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkIssues}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkIssuesTest {

    /**
     * MkIssues can list issues.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesIssues() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.issues().create("hey, you", "body of issue");
        repo.issues().create("hey", "body of 2nd issue");
        repo.issues().create("hey again", "body of 3rd issue");
        MatcherAssert.assertThat(
            repo.issues().iterate(new ArrayMap<String, String>()),
            Matchers.<Issue>iterableWithSize(Tv.THREE)
        );
    }

    /**
     * MkIssues can create a new issue with correct author.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsNewIssueWithCorrectAuthor() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue.Smart issue = new Issue.Smart(
            repo.issues().create("hello", "the body")
        );
        MatcherAssert.assertThat(
            issue.author().login(),
            Matchers.equalTo(repo.github().users().self().login())
        );
    }

    /**
     * MkIssues can create a multiple issues.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsMultipleIssues() throws Exception {
        final Github github = new MkGithub("jeff");
        final Repo repo = github.repos().create(
            new Repos.RepoCreate("test-3", false)
        );
        for (int idx = 1; idx < Tv.TEN; ++idx) {
            repo.issues().create("title", "body");
        }
    }
}
