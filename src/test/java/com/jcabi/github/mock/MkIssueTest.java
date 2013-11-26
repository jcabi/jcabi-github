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

import com.jcabi.github.Issue;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkIssue}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class MkIssueTest {

    /**
     * MkIssue can open and close.
     * @throws Exception If some problem inside
     */
    @Test
    public void opensAndCloses() throws Exception {
        final Issue issue = this.issue();
        MatcherAssert.assertThat(
            new Issue.Smart(issue).isOpen(),
            Matchers.is(true)
        );
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            new Issue.Smart(issue).isOpen(),
            Matchers.is(false)
        );
    }

    /**
     * MkIssue can change title.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesTitle() throws Exception {
        final Issue issue = this.issue();
        new Issue.Smart(issue).title("hey, works?");
        MatcherAssert.assertThat(
            new Issue.Smart(issue).title(),
            Matchers.startsWith("hey, ")
        );
    }

    /**
     * MkIssue can change body.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesBody() throws Exception {
        final Issue issue = this.issue();
        new Issue.Smart(issue).body("hey, body works?");
        MatcherAssert.assertThat(
            new Issue.Smart(issue).body(),
            Matchers.startsWith("hey, b")
        );
    }

    /**
     * Create an issue to work with.
     * @return Issue just created
     * @throws Exception If some problem inside
     */
    private Issue issue() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        ).issues().create("hey", "how are you?");
    }

}
