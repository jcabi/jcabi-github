/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import java.net.HttpURLConnection;
import java.util.EnumMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssues}.
 *
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RtIssuesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtIssues can create an issue.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void createIssue() throws Exception {
        final String title = "Found a bug";
        final String body = issue(title).toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
                .start(this.resource.port())
        ) {
            final RtIssues issues = new RtIssues(
                new JdkRequest(container.home()),
                repo()
            );
            final Issue issue = issues.create(
                title, "having a problem with it."
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new Issue.Smart(issue).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    /**
     * RtIssues can get a single issue.
     * @throws Exception if some problem inside
     */
    @Test
    public void getSingleIssue() throws Exception {
        final String title = "Unit test";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    issue(title).toString()
                )
            ).start(this.resource.port())
        ) {
            final RtIssues issues = new RtIssues(
                new JdkRequest(container.home()),
                repo()
            );
            final Issue issue = issues.get(1);
            MatcherAssert.assertThat(
                new Issue.Smart(issue).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    /**
     * RtIssues can iterate issues.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateIssues() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(issue("new issue"))
                        .add(issue("code issue"))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final RtIssues issues = new RtIssues(
                new JdkRequest(container.home()),
                repo()
            );
            MatcherAssert.assertThat(
                issues.iterate(new ArrayMap<>()),
                Matchers.<Issue>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtIssues can search issues within a repository.
     * @throws Exception if there is any error
     */
    @Test
    public void searchIssues() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(issue("some issue"))
                        .add(issue("some other issue"))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final RtIssues issues = new RtIssues(
                new JdkRequest(container.home()),
                repo()
            );
            MatcherAssert.assertThat(
                issues.search(
                    Issues.Sort.UPDATED,
                    Search.Order.ASC,
                    new EnumMap<>(
                        Issues.Qualifier.class
                    )
                ),
                Matchers.<Issue>iterableWithSize(2)
            );
            container.stop();
        }
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
