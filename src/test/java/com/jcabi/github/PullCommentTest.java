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

import com.jcabi.aspects.Tv;
import javax.json.Json;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link PullComment}.
 *
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
public final class PullCommentTest {

    /**
     * Id field's name in JSON.
     */
    private static final String ID = "id";

    /**
     * Commit id field's name in JSON.
     */
    private static final String COMMIT_ID = "commit_id";

    /**
     * Url field's name in JSON.
     */
    private static final String URL = "url";

    /**
     * Body field's name in JSON.
     */
    private static final String BODY = "body";

    /**
     * PullComment.Smart can fetch the id value from PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).identifier(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the id value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).identifier(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(ID, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the commit id value from PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesCommitId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(COMMIT_ID, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).commitId(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the commit id value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesCommitId() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).commitId(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(COMMIT_ID, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the url value from PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesUrl() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(URL, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).url(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the url value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesUrl() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).url(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(URL, value).build()
        );
    }

    /**
     * PullComment.Smart can fetch the body value from PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void fetchesBody() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        Mockito.doReturn(
            Json.createObjectBuilder().add(BODY, value).build()
        ).when(comment).json();
        MatcherAssert.assertThat(
            new PullComment.Smart(comment).body(),
            Matchers.is(value)
        );
    }

    /**
     * PullComment.Smart can update the body value of PullComment.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesBody() throws Exception {
        final PullComment comment = Mockito.mock(PullComment.class);
        final String value = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        new PullComment.Smart(comment).body(value);
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add(BODY, value).build()
        );
    }
}
