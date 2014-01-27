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

import java.io.IOException;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Pull}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class PullTest {

    /**
     * Pull.Smart can fetch comments count from Pull.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchCommentsCount() throws Exception {
        final int number = 1;
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("comments", number)
                .build()
        ).when(pull).json();
        MatcherAssert.assertThat(
            new Pull.Smart(pull).commentsCount(),
            Matchers.is(number)
        );
    }

    /**
     * Pull.Smart can get an issue where the pull request is submitted.
     */
    @Test
    public void getsIssue() {
        final int number = 2;
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.number()).thenReturn(number);
        final Issues issues = Mockito.mock(Issues.class);
        Mockito.when(issues.get(2)).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.issues()).thenReturn(issues);
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.when(pull.number()).thenReturn(number);
        Mockito.when(pull.repo()).thenReturn(repo);
        MatcherAssert.assertThat(
            new Pull.Smart(pull).issue().number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * Pull.Smart can get pull comments.
     */
    @Test
    public void getsPullComments() throws IOException {

        PullComments pullComments = Mockito.mock(PullComments.class);
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.when(pull.comments()).thenReturn(pullComments);
        MatcherAssert.assertThat(
            new Pull.Smart(pull).comments(),
            Matchers.equalTo(pullComments)
        );
    }

}
