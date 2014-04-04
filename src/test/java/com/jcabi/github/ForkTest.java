/**
 * Copyright (c) 2013-2014, JCabi.com
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

import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Fork}.
 * @author Alexandr Semeshchenko (as777us@gmail.com)
 * @version $Id$
 */
public class ForkTest {
    /**
     * Content.Smart can fetch name property from Fork.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesName() throws Exception {
        final Fork fork = Mockito.mock(Fork.class);
        final String name = "this is some name";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", name)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            new Fork.Smart(fork).name(),
            Matchers.is(name)
        );
    }

    /**
     * Content.Smart can fetch full name property from Fork.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesFullName() throws Exception {
        final Fork fork = Mockito.mock(Fork.class);
        final String fullName = "test full name";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("full_name", fullName)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            new Fork.Smart(fork).fullName(),
            Matchers.is(fullName)
        );
    }

    /**
     * Content.Smart can description property from Fork.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesDescription() throws Exception {
        final Fork fork = Mockito.mock(Fork.class);
        final String description = "test description";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("description", description)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            new Fork.Smart(fork).description(),
            Matchers.is(description)
        );
    }
    /**
     * Content.Smart can fetch size property from Fork.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesSize() throws Exception {
        final Fork fork = Mockito.mock(Fork.class);
        final int prop = 5555;
        Mockito.doReturn(
            Json.createObjectBuilder()
                // @checkstyle MagicNumber (1 line)
                .add("size", prop)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            new Fork.Smart(fork).size(),
            // @checkstyle MagicNumber (1 line)
            Matchers.is(prop)
        );
    }

    /**
     * Content.Smart can urls property from Fork.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesUrls() throws Exception {
        final Fork fork = Mockito.mock(Fork.class);
        final String url = "https://api.github.com/repos/octocat/Hello-World";
        final String html = "https://github.com/octocat/Hello-World";
        final String clone = "https://github.com/octocat/Hello-World.git";
        final String git = "git://github.com/octocat/Hello-World.git";
        final String ssh = "git@github.com:octocat/Hello-World.git";
        final String svn = "https://svn.github.com/octocat/Hello-World";
        final String mirror = "git://git.example.com/octocat/Hello-World";
        final String homepage = "https://github.com";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", url)
                .add("html_url", html)
                .add("clone_url", clone)
                .add("git_url", git)
                .add("ssh_url", ssh)
                .add("svn_url", svn)
                .add("mirror_url", mirror)
                .add("homepage", homepage)
                .build()
        ).when(fork).json();
        final Fork.Smart smartfork = new Fork.Smart(fork);
        MatcherAssert.assertThat(
            smartfork.url().toString(),
            Matchers.is(url)
        );
        MatcherAssert.assertThat(
            smartfork.htmlUrl().toString(),
            Matchers.is(html)
        );
        MatcherAssert.assertThat(
            smartfork.cloneUrl().toString(),
            Matchers.is(clone)
        );
        MatcherAssert.assertThat(
            smartfork.gitUrl().toString(),
            Matchers.is(git)
        );
        MatcherAssert.assertThat(
            smartfork.sshUrl().toString(),
            Matchers.is(ssh)
        );
        MatcherAssert.assertThat(
            smartfork.svnUrl().toString(),
            Matchers.is(svn)
        );
        MatcherAssert.assertThat(
            smartfork.mirrorUrl().toString(),
            Matchers.is(mirror)
        );
        MatcherAssert.assertThat(
            smartfork.homeUrl().toString(),
            Matchers.is(homepage)
        );
    }
    /**
     * Content.Smart can forks_count,stargazers_count,
     * watchers_count properties from Fork.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesCounts() throws Exception {
        final Fork fork = Mockito.mock(Fork.class);
        final int forksCount = 1;
        final int stargazersCount = 2;
        final int watchersCount = 3;
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("forks_count", forksCount)
                .add("stargazers_count", stargazersCount)
                .add("watchers_count", watchersCount)
                .build()
        ).when(fork).json();
        final Fork.Smart smartfork = new Fork.Smart(fork);
        MatcherAssert.assertThat(
            smartfork.forksCount(),
            Matchers.is(forksCount)
        );
        MatcherAssert.assertThat(
            smartfork.stargazersCount(),
            Matchers.is(stargazersCount)
        );
        MatcherAssert.assertThat(
            smartfork.watchersCount(),
            Matchers.is(watchersCount)
        );
    }
    /**
     * Content.Smart can branches properties from Fork.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesBranches() throws Exception {
        final Fork fork = Mockito.mock(Fork.class);
        final String defaultBranch = "master";
        final String masterBranch = "masterBranch";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("default_branch", defaultBranch)
                .add("master_branch", masterBranch)
                .build()
        ).when(fork).json();
        final Fork.Smart smartfork = new Fork.Smart(fork);
        MatcherAssert.assertThat(
            smartfork.defaultBranch(),
            Matchers.is(defaultBranch)
        );
        MatcherAssert.assertThat(
            smartfork.masterBranch(),
            Matchers.is(masterBranch)
        );
    }
}
