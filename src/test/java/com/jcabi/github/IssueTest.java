/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * Test case for {@link Issue}.
 * @since 0.1
 */
final class IssueTest {

    @Test
    void fetchesTitle() throws IOException {
        MatcherAssert.assertThat(
            "Title is not fetched",
            new Issue.Smart(
                IssueTest.issue("title", "this is some text €")
            ).title(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesBody() throws IOException {
        MatcherAssert.assertThat(
            "Body is not fetched",
            new Issue.Smart(
                IssueTest.issue("body", "body of the issue")
            ).body(),
            Matchers.notNullValue()
        );
    }

    @Test
    void detectsPullRequest() throws IOException {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add(
                "pull_request",
                Json.createObjectBuilder().add(
                    "html_url", "http://ibm.com/pulls/3"
                )
            ).build()
        ).when(issue).json();
        final Pulls pulls = Mockito.mock(Pulls.class);
        final Repo repo = Mockito.mock(Repo.class);
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(repo).when(issue).repo();
        Mockito.doReturn(pulls).when(repo).pulls();
        Mockito.when(pulls.get(ArgumentMatchers.eq(3))).thenReturn(pull);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isPull(),
            Matchers.is(true)
        );
        new Issue.Smart(issue).pull();
        Mockito.verify(pulls).get(3);
    }

    @Test
    void detectsPullRequestAbsence() throws IOException {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add(
                "pull_request",
                Json.createObjectBuilder().addNull("html_url")
            ).build()
        ).when(issue).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isPull(),
            Matchers.is(false)
        );
    }

    @Test
    void detectsFullPullRequestAbsence() throws IOException {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.doReturn(
            Json.createObjectBuilder().build()
        ).when(issue).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isPull(),
            Matchers.is(false)
        );
    }

    /**
     * Issue.Smart can fetch issue's labels in read-only mode.
     * @throws IOException If some problem inside.
     */
    @Test
    void fetchLabelsRO() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            IssueTest.roLabels().iterate().iterator().next(),
            Matchers.notNullValue()
        );
    }

    /**
     * Issue.Smart read-only labels cannot add labels.
     * @throws IOException If some problem inside.
     */
    @Test
    void roLabelsCannotAdd() throws IOException {
        final IssueLabels labels = IssueTest.roLabels();
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> labels.add(new ArrayList<>(0)),
            "Read-only labels cannot be modified"
        );
    }

    /**
     * Issue.Smart read-only labels cannot replace labels.
     * @throws IOException If some problem inside.
     */
    @Test
    void roLabelsCannotReplace() throws IOException {
        final IssueLabels labels = IssueTest.roLabels();
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> labels.replace(new ArrayList<>(0)),
            "Read-only labels cannot be modified"
        );
    }

    /**
     * Issue.Smart read-only labels cannot remove labels.
     * @throws IOException If some problem inside.
     */
    @Test
    void roLabelsCannotRemove() throws IOException {
        final IssueLabels labels = IssueTest.roLabels();
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> labels.remove("bug"),
            "Read-only labels cannot be modified"
        );
    }

    /**
     * Issue.Smart read-only labels cannot clear labels.
     * @throws IOException If some problem inside.
     */
    @Test
    void roLabelsCannotClear() throws IOException {
        final IssueLabels labels = IssueTest.roLabels();
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> labels.clear(),
            "Read-only labels cannot be modified"
        );
    }

    /**
     * Issue.Smart read-only label cannot be patched.
     * @throws IOException If some problem inside.
     */
    @Test
    void roLabelCannotBePatchedTest() throws IOException {
        final IssueLabels labels = IssueTest.roLabels();
        final Label label = labels.iterate().iterator().next();
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> label.patch(Mockito.mock(JsonObject.class)),
            "Read-only label cannot be modified"
        );
    }

    /**
     * Mock repo for GhIssue creation.
     * @return The mock repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("user").when(coords).user();
        Mockito.doReturn("repo").when(coords).repo();
        return repo;
    }

    /**
     * Read-only labels of an issue with one label in it.
     * @return Labels
     * @throws IOException If fails
     */
    private static IssueLabels roLabels() throws IOException {
        return new Issue.Smart(
            new RtIssue(
                new FakeRequest().withBody(
                    Json.createObjectBuilder().add(
                        "labels",
                        Json.createArrayBuilder().add(
                            Json.createObjectBuilder()
                                .add("name", "bug")
                                .add("color", "f29513")
                        )
                    ).build().toString()
                ), IssueTest.repo(), 1
            )
        ).roLabels();
    }

    /**
     * Issue with a single property.
     * @param key Name of the property
     * @param value Value of the property
     * @return Issue
     * @throws IOException If fails
     */
    private static Issue issue(final String key, final String value)
        throws IOException {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add(key, value)
                .build()
        ).when(issue).json();
        return issue;
    }
}
