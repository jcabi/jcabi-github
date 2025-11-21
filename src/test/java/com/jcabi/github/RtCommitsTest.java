/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

/**
 * Testcase for RtCommits.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtCommitsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void createsCommit() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"sha\":\"0abcd89jcabitest\"}"
                )
            ).start(RandomPort.port())) {
            final Commits commits = new RtCommits(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            final JsonObject author = Json.createObjectBuilder()
                .add("name", "Scott").add("email", "scott@gmail.com")
                .add("date", "2011-06-17T14:53:35-07:00").build();
            final JsonObject input = Json.createObjectBuilder()
                .add("message", "initial version")
                .add("author", author).build();
            final Commit commit = commits.create(input);
            MatcherAssert.assertThat(
                "Object is not of expected type",
                commit,
                Matchers.instanceOf(Commit.class)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                commit.sha(),
                Matchers.equalTo("0abcd89jcabitest")
            );
        }
    }
}
