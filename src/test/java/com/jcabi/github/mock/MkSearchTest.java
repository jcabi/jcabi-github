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
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Search;
import java.util.EnumMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkSearch}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MkSearchTest {

    /**
     * MkSearch can search for repos.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    void canSearchForRepos() throws Exception {
        final MkGithub github = new MkGithub();
        github.repos().create(
            new Repos.RepoCreate("TestRepo", false)
        );
        MatcherAssert.assertThat(
            github.search().repos("TestRepo", "updated", Search.Order.ASC),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkSearch can search for issues.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    void canSearchForIssues() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.repos().create(
            new Repos.RepoCreate("TestIssues", false)
        );
        repo.issues().create("test issue", "TheTest");
        MatcherAssert.assertThat(
            github.search().issues(
                "TheTest",
                "updated",
                Search.Order.DESC,
                new EnumMap<Search.Qualifier, String>(Search.Qualifier.class)
            ),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkSearch can search for users.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    void canSearchForUsers() throws Exception {
        final MkGithub github = new MkGithub("jeff");
        github.users().self();
        MatcherAssert.assertThat(
            github.search().users("jeff", "repositories", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkSearch can search for codes.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    void canSearchForCodes() throws Exception {
        final MkGithub github = new MkGithub("jeff");
        github.repos().create(
            new Repos.RepoCreate("TestCode", false)
        );
        MatcherAssert.assertThat(
            github.search().codes("jeff", "repositories", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
