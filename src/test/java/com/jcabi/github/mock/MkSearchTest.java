/**
 * Copyright (c) 2012-2013, JCabi.com
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

import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.User;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkSearch}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class MkSearchTest {

    /**
     * MkSearch can search for repos.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForRepos() throws Exception {
        final String name = "repo";
        final Github github = new MkGithub();
        final Repo repo = github.repos().create(
            // @checkstyle MultipleStringLiterals (4 lines)
            Json.createObjectBuilder().add("name", name).build()
        );
        MatcherAssert.assertThat(
            github.search().repos(name, "stars", "desc").iterator().next(),
            Matchers.equalTo(repo)
        );
    }

    /**
     * MkSearch can search for issues.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForIssues() throws Exception {
        final String name = "issue";
        final Github github = new MkGithub();
        final Issue issue = github.repos().create(
            Json.createObjectBuilder().add("name", "repo1").build()
        ).issues().create(name, "body");
        MatcherAssert.assertThat(
            github.search().issues(name, "created", "desc").iterator().next(),
            Matchers.equalTo(issue)
        );
    }

    /**
     * MkSearch can search for users.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForUsers() throws Exception {
        final Github github = new MkGithub();
        final User user = github.users().self();
        MatcherAssert.assertThat(
            github.search().users(user.login(), "followers", "desc")
                .iterator().next(),
            Matchers.equalTo(user)
        );
    }

}
