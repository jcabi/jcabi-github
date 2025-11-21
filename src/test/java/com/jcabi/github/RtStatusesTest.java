/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Testcase for {@link RtStatuses}.
 * @since 0.24
 * @todo #1130:30min Write RtStatusesITCase, an integration test case for
 *  RtStatuses/RtStatus against real GitHub commit status data.
 * @todo #1490:30min Continue to close grizzle servers open on tests. Use
 *  try-with-resource statement instead of try-catch whenever is possible.
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@ExtendWith(RandomPort.class)
final class RtStatusesTest {

    /**
     * RtStatuses can fetch its commit.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    void fetchesCommit() throws IOException {
        final Commit original = new MkGitHub().randomRepo().git()
            .commits().get("5e8d65e0dbfab0716db16493e03a0baba480625a");
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtStatuses(new FakeRequest(), original).commit(),
            Matchers.equalTo(original)
        );
    }

    @Test
    void createsStatus() throws IOException {
        final String stateprop = "state";
        final String urlprop = "target_url";
        final String descprop = "description";
        final String contextprop = "context";
        final String url = "https://ci.example.com/1000/output";
        final String description = "Build has completed successfully";
        final String context = "continuous-integration/jenkins";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().add(stateprop, "failure")
                    .add(urlprop, url)
                    .add(descprop, description)
                    .add(contextprop, context)
                    .build().toString()
            )
        ).start(RandomPort.port());
        final Request entry = new ApacheRequest(container.home());
        final Statuses statuses = new RtStatuses(
            entry,
            new RtCommit(
                entry,
                new MkGitHub().randomRepo(),
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
                "Values are not equal",
                request.method(),
                Matchers.equalTo(Request.POST)
            );
            final JsonObject obj = Json.createReader(
                new StringReader(request.body())
            ).readObject();
            MatcherAssert.assertThat(
                "Values are not equal",
                obj.getString(stateprop),
                Matchers.equalTo(Status.State.FAILURE.identifier())
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                obj.getString(contextprop),
                Matchers.equalTo(context)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                obj.getString(descprop),
                Matchers.equalTo(description)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                obj.getString(urlprop),
                Matchers.equalTo(url)
            );
        } finally {
            container.stop();
        }
    }
}
