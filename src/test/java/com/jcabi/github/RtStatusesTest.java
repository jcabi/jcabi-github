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

import com.google.common.base.Optional;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Testcase for {@link RtStatuses}.
 *
 * @author Marcin Cylke (marcin.cylke+github@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @todo #1130:30min Write RtStatusesITCase, an integration test case for
 *  RtStatuses/RtStatus against real GitHub commit status data.
 * @todo #1490:30min Continue to close grizzle servers open on tests. Use
 *  try-with-resource statement instead of try-catch whenever is possible.
 */
public final class RtStatusesTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtStatuses can fetch its commit.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesCommit() throws IOException {
        final Commit original = new MkGithub().randomRepo().git()
            .commits().get("5e8d65e0dbfab0716db16493e03a0baba480625a");
        MatcherAssert.assertThat(
            new RtStatuses(new FakeRequest(), original).commit(),
            Matchers.equalTo(original)
        );
    }

    /**
     * Tests creating a Status.
     *
     * @throws Exception when an Error occurs
     */
    @Test
    public void createsStatus() throws Exception {
        final String stateprop = "state";
        final String urlprop = "target_url";
        final String descriptionprop = "description";
        final String contextprop = "context";
        final String url = "https://ci.example.com/1000/output";
        final String description = "Build has completed successfully";
        final String context = "continuous-integration/jenkins";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().add(stateprop, "failure")
                    .add(urlprop, url)
                    .add(descriptionprop, description)
                    .add(contextprop, context)
                    .build().toString()
            )
        ).start(this.resource.port());
        final Request entry = new ApacheRequest(container.home());
        final Statuses statuses = new RtStatuses(
            entry,
            new RtCommit(
                entry,
                new MkGithub().randomRepo(),
                "0abcd89jcabitest"
            )
        );
        try {
            statuses.create(
                new Statuses.StatusCreate(Status.State.FAILURE)
                    .withTargetUrl(Optional.of(url))
                    .withDescription(description)
                    .withContext(Optional.of(context))
            );
            final MkQuery request = container.take();
            MatcherAssert.assertThat(
                request.method(),
                Matchers.equalTo(Request.POST)
            );
            final JsonObject obj = Json.createReader(
                new StringReader(request.body())
            ).readObject();
            MatcherAssert.assertThat(
                obj.getString(stateprop),
                Matchers.equalTo(Status.State.FAILURE.identifier())
            );
            MatcherAssert.assertThat(
                obj.getString(contextprop),
                Matchers.equalTo(context)
            );
            MatcherAssert.assertThat(
                obj.getString(descriptionprop),
                Matchers.equalTo(description)
            );
            MatcherAssert.assertThat(
                obj.getString(urlprop),
                Matchers.equalTo(url)
            );
        } finally {
            container.stop();
        }
    }
}
