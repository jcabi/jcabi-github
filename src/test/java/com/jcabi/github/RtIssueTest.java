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

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssue}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtIssueTest {

    /**
     * RtIssue should be able to fetch its comments.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesComments() throws Exception {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.comments(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to fetch its labels.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesLabels() throws Exception {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.labels(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to fetch its events.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesEvents() throws Exception {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.events(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to describe itself in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchIssueAsJson() throws Exception {
        final RtIssue issue = new RtIssue(
            new FakeRequest().withBody("{\"issue\":\"json\"}"),
            this.repo(),
            1
        );
        MatcherAssert.assertThat(
            issue.json().getString("issue"),
            Matchers.equalTo("json")
        );
    }

    /**
     * RtIssue should be able to perform a patch request.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void patchWithJson() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
        ).start();
        final RtIssue issue = new RtIssue(
            new ApacheRequest(container.home()),
            this.repo(),
            1
        );
        issue.patch(
            Json.createObjectBuilder().add("patch", "test").build()
        );
        final MkQuery query = container.take();
        try {
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtIssue should be able to compare different instances.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final RtIssue less = new RtIssue(new FakeRequest(), this.repo(), 1);
        final RtIssue greater = new RtIssue(new FakeRequest(), this.repo(), 2);
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * Mock repo for GhIssue creation.
     * @return The mock repo.
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("user").when(coords).user();
        Mockito.doReturn("repo").when(coords).repo();
        return repo;
    }

}
