/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * Test case for {@link Issue}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class IssueTest {

    /**
     * Rule for checking thrown exception.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    @SuppressWarnings("deprecation")
    public transient ExpectedException thrown = ExpectedException.none();

    @Test
    public void fetchesProperties() throws IOException {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("title", "this is some text \u20ac")
                .add("body", "body of the issue")
                .build()
        ).when(issue).json();
        final Issue.Smart smart = new Issue.Smart(issue);
        MatcherAssert.assertThat(
            "Value is null",
            smart.title(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Value is null",
            smart.body(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void detectsPullRequest() throws IOException {
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
        Mockito.when(pulls.get(ArgumentMatchers.eq(Tv.THREE))).thenReturn(pull);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isPull(),
            Matchers.is(true)
        );
        new Issue.Smart(issue).pull();
        Mockito.verify(pulls).get(Tv.THREE);
    }

    @Test
    public void detectsPullRequestAbsence() throws IOException {
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
    public void detectsFullPullRequestAbsence() throws IOException {
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
    public void fetchLabelsRO() throws IOException {
        final String name = "bug";
        final JsonObject json = Json.createObjectBuilder().add(
            "labels",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("name", name)
                    .add("color", "f29513")
            )
        ).build();
        final Issue issue = new RtIssue(
            new FakeRequest().withBody(json.toString()), this.repo(), 1
        );
        final IssueLabels labels = new Issue.Smart(issue).roLabels();
        this.thrown.expect(UnsupportedOperationException.class);
        labels.add(new ArrayList<>(0));
        this.thrown.expect(UnsupportedOperationException.class);
        labels.replace(new ArrayList<>(0));
        this.thrown.expect(UnsupportedOperationException.class);
        labels.remove(name);
        this.thrown.expect(UnsupportedOperationException.class);
        labels.clear();
        final Label label = labels.iterate().iterator().next();
        MatcherAssert.assertThat(
            "Value is null", label, Matchers.notNullValue()
        );
        this.thrown.expect(UnsupportedOperationException.class);
        label.patch(Mockito.mock(JsonObject.class));
    }

    /**
     * Mock repo for GhIssue creation.
     * @return The mock repo.
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("user").when(coords).user();
        Mockito.doReturn("repo").when(coords).repo();
        return repo;
    }
}
