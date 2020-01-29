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

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPull}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtPullTest {
    /**
     * Property name for ref name in pull request ref JSON object.
     */
    private static final String REF_PROP = "ref";
    /**
     * Property name for commit SHA in pull request ref JSON object.
     */
    private static final String SHA_PROP = "sha";

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPull should be able to retrieve commits.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void fetchesCommits() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"commits\":\"test\"}]"
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                1
            );
            MatcherAssert.assertThat(
                pull.commits(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * RtPull should be able to retrieve files.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void fetchesFiles() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"file1\":\"testFile\"}]"
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                2
            );
            MatcherAssert.assertThat(
                pull.files().iterator().next().getString("file1"),
                Matchers.equalTo("testFile")
            );
            container.stop();
        }
    }

    /**
     * RtPull can fetch its base ref.
     * @throws IOException If some I/O problem occurs
     */
    @Test
    public void fetchesBase() throws IOException {
        final String ref = "sweet-feature-branch";
        final String sha = "e93c6a2216c69daa574abc16e7c14767fce44ad6";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add(
                            "base",
                            Json.createObjectBuilder()
                                .add(RtPullTest.REF_PROP, ref)
                                .add(RtPullTest.SHA_PROP, sha)
                                .build()
                        )
                        .build()
                        .toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                1
            );
            final PullRef base = pull.base();
            MatcherAssert.assertThat(
                base,
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                base.ref(),
                Matchers.equalTo(ref)
            );
            MatcherAssert.assertThat(
                base.sha(),
                Matchers.equalTo(sha)
            );
            container.stop();
        }
    }

    /**
     * RtPull can fetch its head ref.
     * @throws IOException If some I/O problem occurs
     */
    @Test
    public void fetchesHead() throws IOException {
        final String ref = "neat-other-branch";
        final String sha = "9c717b4716e4fc4d917f546e8e6b562e810e3922";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add(
                            "head",
                            Json.createObjectBuilder()
                                .add(RtPullTest.REF_PROP, ref)
                                .add(RtPullTest.SHA_PROP, sha)
                                .build()
                        )
                        .build()
                        .toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                1
            );
            final PullRef head = pull.head();
            MatcherAssert.assertThat(
                head,
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                head.ref(),
                Matchers.equalTo(ref)
            );
            MatcherAssert.assertThat(
                head.sha(),
                Matchers.equalTo(sha)
            );
            container.stop();
        }
    }

    /**
     * RtPull should be able to perform a merge.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void executeMerge() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testMerge")
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                3
            );
            pull.merge("Test commit.");
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.equalTo("{\"commit_message\":\"Test commit.\"}")
            );
            container.stop();
        }
    }

    /**
     * RtPull should be able to compare different instances.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final RtPull less = new RtPull(new FakeRequest(), this.repo(), 1);
        final RtPull greater = new RtPull(new FakeRequest(), this.repo(), 2);
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * RtPull should be able to fetch pull comments.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    @Ignore
    public void canFetchComments() throws Exception {
        //to be implemented
    }

    /**
     * Mock repository for testing purposes.
     * @return Repo the mock repository.
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("/user").when(coords).user();
        Mockito.doReturn("/repo").when(coords).repo();
        final Github github = Mockito.mock(Github.class);
        Mockito.doReturn(github).when(repo).github();
        Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();
        return repo;
    }

}
