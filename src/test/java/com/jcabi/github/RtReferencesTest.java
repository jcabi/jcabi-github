/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtReferences}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReferencesTest {

    /**
     * The rule for skipping test if there's BindException.
     *  and make MkGrizzlyContainers use port() given by this resource to avoid
     *  tests fail with BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtReferences should create and return a Reference.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void createsReference() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"ref\":\"refs/heads/feature-a\"}"
            )
        ).start(this.resource.port());
        final References refs = new RtReferences(
            new ApacheRequest(container.home()),
            new MkGithub().randomRepo()
        );
        try {
            MatcherAssert.assertThat(
                refs.create("abceefgh3456", "refs/heads/feature-a"),
                Matchers.instanceOf(Reference.class)
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReferences should be able to iterate over References.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesReferences() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"ref\":\"refs/heads/feature-a\"}"
            )
        ).start(this.resource.port());
        final References refs = new RtReferences(
            new ApacheRequest(container.home()),
            new MkGithub().randomRepo()
        );
        try {
            MatcherAssert.assertThat(
                refs.iterate(),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReferences should be able to remove a Reference.
     * @throws Exception - If somethins goes wrong.
     */
    @Test
    public void removesReference() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start(this.resource.port());
        final References refs = new RtReferences(
            new ApacheRequest(container.home()),
            new MkGithub().randomRepo()
        );
        refs.remove("heads/feature-a");
        try {
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReferences should be able to iterate over tags.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesTags() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "[{\"ref\":\"refs/tags/feature-b\"}]"
            )
        ).start(this.resource.port());
        final References refs = new RtReferences(
            new ApacheRequest(container.home()),
            new MkGithub().randomRepo()
        );
        try {
            MatcherAssert.assertThat(
                refs.tags(),
                Matchers.<Reference>iterableWithSize(1)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/tags")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtReferences should be able to iterate over heads.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesHeads() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "[{\"ref\":\"refs/heads/feature-c\"}]"
            )
        ).start(this.resource.port());
        final References refs = new RtReferences(
            new ApacheRequest(container.home()),
            new MkGithub().randomRepo()
        );
        try {
            MatcherAssert.assertThat(
                refs.heads(),
                Matchers.<Reference>iterableWithSize(1)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/heads")
            );
        } finally {
            container.stop();
        }
    }
}
