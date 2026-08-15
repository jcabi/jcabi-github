/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.immutable.ArrayMap;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.EnumMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssues}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtIssuesTest {

    @Test
    void createsIssueWithPost() throws IOException {
        final String title = "Found a bug";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtIssuesTest.answer(title))
                .start(RandomPort.port())
        ) {
            RtIssuesTest.create(container, title);
            MatcherAssert.assertThat(
                "Issue is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsIssueWithTitle() throws IOException {
        final String title = "Found a bug";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtIssuesTest.answer(title))
                .next(RtIssuesTest.fetched(title))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created issue has a wrong title",
                new Issue.Smart(RtIssuesTest.create(container, title)).title(),
                Matchers.equalTo(title)
            );
        }
    }

    @Test
    void fetchesSingleIssue() throws IOException {
        final String title = "Unit test";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtIssuesTest.issue(title).toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                new Issue.Smart(
                    new RtIssues(
                        new JdkRequest(container.home()),
                        RtIssuesTest.repo()
                    ).get(1)
                ).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    @Test
    void iterateIssues() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtIssuesTest.issue("new issue"))
                        .add(RtIssuesTest.issue("code issue"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtIssues(
                    new JdkRequest(container.home()),
                    RtIssuesTest.repo()
                ).iterate(new ArrayMap<>()),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void searchIssues() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtIssuesTest.issue("some issue"))
                        .add(RtIssuesTest.issue("some other issue"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtIssues(
                    new JdkRequest(container.home()),
                    RtIssuesTest.repo()
                ).search(
                    Issues.Sort.UPDATED,
                    Search.Order.ASC,
                    new EnumMap<>(
                        Issues.Qualifier.class
                    )
                ),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * Create an issue through the given container.
     * @param container Container to serve the issues
     * @param title Title of the issue
     * @return Created issue
     * @throws IOException If there is any I/O problem
     */
    private static Issue create(
        final MkContainer container,
        final String title
    ) throws IOException {
        return new RtIssues(
            new JdkRequest(container.home()),
            RtIssuesTest.repo()
        ).create(title, "having a problem with it.");
    }

    /**
     * Answer with an issue of the given title.
     * @param title Title of the issue
     * @return Answer
     */
    private static MkAnswer answer(final String title) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            RtIssuesTest.issue(title).toString()
        );
    }

    /**
     * Answer with a fetched issue of the given title.
     * @param title Title of the issue
     * @return Answer
     */
    private static MkAnswer fetched(final String title) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtIssuesTest.issue(title).toString()
        );
    }

    /**
     * Create and return JsonObject to test.
     * @param title The title of the issue
     * @return JsonObject
     */
    private static JsonObject issue(final String title) {
        return Json.createObjectBuilder()
            .add("number", 1)
            .add("state", Issue.OPEN_STATE)
            .add("title", title)
            .build();
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
