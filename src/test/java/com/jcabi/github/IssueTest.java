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

import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Issue}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class IssueTest {

    /**
     * IssueMocker can open and close.
     * @throws Exception If some problem inside
     */
    @Test
    public void opensAndCloses() throws Exception {
        final Issue issue = this.issue();
        MatcherAssert.assertThat(
            new Issue.Tool(issue).isOpen(),
            Matchers.is(true)
        );
        new Issue.Tool(issue).close();
        MatcherAssert.assertThat(
            new Issue.Tool(issue).isOpen(),
            Matchers.is(false)
        );
    }

    /**
     * IssueMocker can change title.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesTitle() throws Exception {
        final Issue issue = this.issue();
        new Issue.Tool(issue).title("hey, works?");
        MatcherAssert.assertThat(
            new Issue.Tool(issue).title(),
            Matchers.startsWith("hey, ")
        );
    }

    /**
     * IssueMocker can change body.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesBody() throws Exception {
        final Issue issue = this.issue();
        new Issue.Tool(issue).body("hey, body works?");
        MatcherAssert.assertThat(
            new Issue.Tool(issue).body(),
            Matchers.startsWith("hey, b")
        );
    }

    /**
     * Issue.Tool can detect a pull request.
     * @throws Exception If some problem inside
     */
    @Test
    public void detectsPullRequest() throws Exception {
        final Issue issue = this.issue();
        Mockito.doReturn(
            Json.createObjectBuilder().add(
                "pull_request",
                Json.createObjectBuilder().add("url", "http://ibm.com/pulls/2")
            ).build()
        ).when(issue).json();
        MatcherAssert.assertThat(
            new Issue.Tool(issue).isPull(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new Issue.Tool(issue).pull().number(),
            Matchers.equalTo(2)
        );
    }

    /**
     * Create an issue to work with.
     * @return Issue just created
     * @throws Exception If some problem inside
     */
    private Issue issue() throws Exception {
        return new GithubMocker().createRepo("tt/a")
            .issues().create("hey", "how are you?");
    }

}
