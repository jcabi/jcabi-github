/**
 * Copyright (c) 2013-2020, jcabi.com
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
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.immutable.ArrayMap;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPulls}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (200 lines)
 */
final class RtPullsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPulls can create a pull request.
     *
     * @throws Exception if some problem inside
     */
    @Test
    void createPull() throws Exception {
        final String title = "new feature";
        final String body = pull(title).toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
                .start(this.resource.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                repo()
            );
            final Pull pull = pulls.create(title, "octocat", "master");
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new Pull.Smart(pull).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    /**
     * RtPulls can get a single pull request.
     * @throws Exception if some problem inside
     */
    @Test
    void getSinglePull() throws Exception {
        final String title = "new-feature";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    pull(title).toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                repo()
            );
            final Pull pull = pulls.get(Tv.BILLION);
            MatcherAssert.assertThat(
                new Pull.Smart(pull).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    /**
     * RtPulls can iterate pulls.
     * @throws Exception if there is any error
     */
    @Test
    void iteratePulls() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(pull("new-topic"))
                        .add(pull("Amazing new feature"))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                repo()
            );
            MatcherAssert.assertThat(
                pulls.iterate(new ArrayMap<String, String>()),
                Matchers.<Pull>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param title The title of the pull request
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject pull(final String title) throws Exception {
        return Json.createObjectBuilder()
            .add("number", Tv.BILLION)
            .add("state", Issue.OPEN_STATE)
            .add("title", title)
            .build();
    }

    /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
