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
import javax.json.JsonObject;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Mocker of {@link Issue}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class IssueMocker implements Issue {

    /**
     * Mocked comment.
     */
    private final transient Issue issue = Mockito.mock(Issue.class);

    /**
     * Public ctor.
     * @param repo Owner of it
     * @param number Number of it
     * @throws IOException If fails
     */
    public IssueMocker(final Repo repo, final int number) throws IOException {
        Mockito.doReturn(repo).when(this.issue).repo();
        Mockito.doReturn(number).when(this.issue).number();
        Mockito.doReturn(new CommentsMocker(this)).when(this.issue).comments();
        Mockito.doReturn(new LabelsMocker()).when(this.issue).labels();
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("title", "test issue title")
                .add("body", "test body")
                .add("state", "open")
                .build()
        ).when(this.issue).json();
        Mockito.doAnswer(
            new Answer<Void>() {
                @Override
                public Void answer(final InvocationOnMock inv)
                    throws IOException {
                    IssueMocker.this.patch(
                        JsonObject.class.cast(inv.getArguments()[0])
                    );
                    return null;
                }
            }
        ).when(this.issue).patch(Mockito.any(JsonObject.class));
    }

    @Override
    public Repo repo() {
        return this.issue.repo();
    }

    @Override
    public int number() {
        return this.issue.number();
    }

    @Override
    public Comments comments() {
        return this.issue.comments();
    }

    @Override
    public Labels labels() {
        return this.issue.labels();
    }

    @Override
    public JsonObject json() throws IOException {
        return this.issue.json();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        Mockito.doReturn(new JsonMocker(this.issue.json()).patch(json))
            .when(this.issue).json();
    }

    @Override
    public int compareTo(final Issue iss) {
        return new Integer(this.number()).compareTo(iss.number());
    }

    /**
     * Get mocked object.
     * @return Mocked object
     */
    public Issue mock() {
        return this.issue;
    }

}
