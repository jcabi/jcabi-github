/**
 * Copyright (c) 2013-2018, jcabi.com
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
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPullComment}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (3 lines)
 */
public final class RtPullCommentTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();
    /**
     * RtPullComment should be able to compare different instances.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(new MkGithub().randomRepo()).when(pull).repo();
        final RtPullComment less =
            new RtPullComment(new FakeRequest(), pull, 1);
        final RtPullComment greater =
            new RtPullComment(new FakeRequest(), pull, 2);
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
        MatcherAssert.assertThat(
            less.compareTo(less), Matchers.equalTo(0)
        );
    }

    /**
     * RtPullComment can return its JSON description.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canDescribeAsJson() throws Exception {
        final String body = "{\"body\":\"test\"}";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body)
            ).start(this.resource.port())
        ) {
            final Pull pull = Mockito.mock(Pull.class);
            Mockito.doReturn(repo()).when(pull).repo();
            final RtPullComment comment =
                new RtPullComment(new ApacheRequest(container.home()), pull, 1);
            final JsonObject json = comment.json();
            MatcherAssert.assertThat(
                json.getString("body"),
                Matchers.is("test")
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith("/repos/joe/blueharvest/pulls/comments/1")
            );
            container.stop();
        }
    }

    /**
     * RtPullComment can create a patch request.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void patchesComment() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(this.resource.port())
        ) {
            final Pull pull = Mockito.mock(Pull.class);
            Mockito.doReturn(repo()).when(pull).repo();
            final RtPullComment comment =
                new RtPullComment(new ApacheRequest(container.home()), pull, 2);
            final JsonObject json = Json.createObjectBuilder()
                .add("body", "test comment").build();
            comment.patch(json);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(), Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.containsString("{\"body\":\"test comment\"}")
            );
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/repos/joe/blueharvest/pulls/comments/2")
            );
            container.stop();
        }
    }

    /**
     * RtPullComment can add a reaction.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    @Ignore
    public void reacts() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Pull pull = repo.pulls().create(
                "Reaction adding test",
                "This is a test for adding a reaction",
                "Base"
            );
            final RtPullComment comment = new RtPullComment(
                new ApacheRequest(container.home()), pull, 2
            );
            comment.react(new Reaction.Simple(Reaction.HEART));
            MatcherAssert.assertThat(
                "Pull comment was unable to react",
                comment.reactions(),
                new IsCollectionWithSize<>(
                    new IsEqual<>(1)
                )
            );
        }
    }

    /**
     * This method returns a Repo for testing.
     * @return Repo - a repo to be used for test.
     * @throws Exception - if anything goes wrong.
     */
    private static Repo repo() throws Exception {
        return new MkGithub("joe").repos().create(
            new Repos.RepoCreate("blueharvest", false)
        );
    }
}
