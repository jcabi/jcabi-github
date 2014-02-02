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
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtGithub}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtGithubTest {

    /**
     * RtGithub can retrieve its repos.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesRepos() throws Exception {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.repos(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve its gists.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesGists() throws Exception {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.gists(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve users.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesUsers() throws Exception {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.users(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve meta information in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesMetaAsJson() throws Exception {
        final RtGithub github = new RtGithub(
            new FakeRequest().withBody("{\"meta\":\"blah\"}")
        );
        MatcherAssert.assertThat(
            github.meta().getString("meta"),
            Matchers.equalTo("blah")
        );
    }

    /**
     * RtGithub can retrieve meta information in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesEmojisAsJson() throws Exception {
        final RtGithub github = new RtGithub(
            new FakeRequest().withBody("{\"emojis\":\"test\"}")
        );
        MatcherAssert.assertThat(
            github.meta().getString("emojis"),
            Matchers.equalTo("test")
        );
    }

    /**
     * RtGithub can retrieve the markdown.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesMarkdown() throws Exception {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.markdown(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtGithub can retrieve the gitignores.
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesGitignores() throws Exception {
        final RtGithub github = new RtGithub(new FakeRequest());
        MatcherAssert.assertThat(
            github.gitignores(),
            Matchers.notNullValue()
        );
    }

}
