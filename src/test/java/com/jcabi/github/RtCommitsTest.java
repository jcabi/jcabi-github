/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Testcase for RtCommits.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtCommitsTest {

    @Test
    void createsCommitWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"sha\":\"0abcd89jcabitest\"}"
                )
            ).start(RandomPort.port())
        ) {
            RtCommitsTest.create(container);
            MatcherAssert.assertThat(
                "Commit is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsCommitWithSha() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"sha\":\"0abcd89jcabitest\"}"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created commit has a wrong SHA",
                RtCommitsTest.create(container).sha(),
                Matchers.equalTo("0abcd89jcabitest")
            );
        }
    }

    /**
     * Create a commit through the given container.
     * @param container Container to serve the request
     * @return Created commit
     * @throws IOException If there is any I/O problem
     */
    private static Commit create(final MkContainer container)
        throws IOException {
        return new RtCommits(
            new ApacheRequest(container.home()),
            new MkGitHub().randomRepo()
        ).create(
            Json.createObjectBuilder()
                .add("message", "initial version").add(
                    "author",
                    Json.createObjectBuilder()
                        .add("name", "Scott")
                        .add("email", "scott@gmail.com")
                        .add("date", "2011-06-17T14:53:35-07:00").build()
                ).build()
        );
    }
}
