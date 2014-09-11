/**
 * Copyright (c) 2013-2014, jcabi.com
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
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRepo}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RtRepoTest {
    /**
     * RtRepo can fetch events.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesEvents() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(event(Event.ASSIGNED))
                    .add(event(Event.MENTIONED))
                    .build().toString()
            )
        ).start();
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home()),
            new Coordinates.Simple("octocat", "master")
        );
        MatcherAssert.assertThat(
            repo.events(),
            Matchers.<Event>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * RtRepo can fetch its labels.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesLabels() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("jeff", "jeff-branch")
        );
        MatcherAssert.assertThat(
            repo.labels(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its issues.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesIssues() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("mark", "mark-branch")
        );
        MatcherAssert.assertThat(
            repo.issues(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its pulls.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesPulls() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("kendy", "kendy-branch")
        );
        MatcherAssert.assertThat(
            repo.pulls(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its hooks.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchHooks() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("paul", "paul-branch")
        );
        MatcherAssert.assertThat(
            repo.hooks(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its keys.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchKeys() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("andres", "andres-branch")
        );
        MatcherAssert.assertThat(
            repo.keys(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its releases.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchReleases() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("phil", "phil-branch")
        );
        MatcherAssert.assertThat(
            repo.releases(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its contents.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchContents() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("andres-contents", "contents-branch")
        );
        MatcherAssert.assertThat(
            repo.contents(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can identify itself.
     * @throws Exception If some problem inside
     */
    @Test
    public void identifiesItself() throws Exception {
        final Coordinates coords = new Coordinates.Simple("me", "me-branch");
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            coords
        );
        MatcherAssert.assertThat(
            repo.coordinates(),
            Matchers.sameInstance(coords)
        );
    }

    /**
     * RtRepo can execute PATCH request.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void executePatchRequest() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                event(Event.ASSIGNED).toString()
            )
        ).start();
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home()),
            new Coordinates.Simple("test", "test-branch")
        );
        repo.patch(event(Event.ASSIGNED));
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.PATCH)
        );
        container.stop();
    }

    /**
     * RtRepo can describe as a JSON object.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void describeAsJson() throws Exception {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("full_name", "octocat/Hello-World")
                    .add("fork", true)
                    .build()
                    .toString()
            ),
            new Coordinates.Simple("oct", "oct-branch")
        );
        MatcherAssert.assertThat(
            repo.json().toString(),
            Matchers.equalTo(
                "{\"full_name\":\"octocat/Hello-World\",\"fork\":true}"
            )
        );
    }

    /**
     * RtRepo can fetch commits.
     */
    @Test
    public void fetchCommits() {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("testuser2", "testrepo2")
        );
        MatcherAssert.assertThat(repo.commits(), Matchers.notNullValue());
    }

    /**
     * RtRepo can star an unstarred Repo.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void starARepo() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start();
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home()),
            new Coordinates.Simple("testuser", "testrepo")
        );
        try {
            repo.star();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.isEmptyOrNullString()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtRepo can fetch Git.
     */
    public void fetchesGit() {
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            new Coordinates.Simple("gituser", "gitrepo")
        );
        MatcherAssert.assertThat(repo.git(), Matchers.notNullValue());
    }

    /**
     * Create and return JsonObject to test.
     * @param event Event type
     * @return JsonObject
     * @throws Exception if some problem inside
     */
    private static JsonObject event(final String event) throws Exception {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("event", event)
            .build();
    }
}
