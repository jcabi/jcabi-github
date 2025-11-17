/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssue}.
 *
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RtIssueTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtIssue should be able to fetch its comments.
     *
     */
    @Test
    public void fetchesComments() {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.comments(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to fetch its labels.
     *
     */
    @Test
    public void fetchesLabels() {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.labels(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to fetch its events.
     *
     */
    @Test
    public void fetchesEvents() {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.events(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to describe itself in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchIssueAsJson() throws Exception {
        final RtIssue issue = new RtIssue(
            new FakeRequest().withBody("{\"issue\":\"json\"}"),
            this.repo(),
            1
        );
        MatcherAssert.assertThat(
            issue.json().getString("issue"),
            Matchers.equalTo("json")
        );
    }

    /**
     * RtIssue should be able to perform a patch request.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void patchWithJson() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(this.resource.port())
        ) {
            final RtIssue issue = new RtIssue(
                new ApacheRequest(container.home()),
                this.repo(),
                1
            );
            issue.patch(
                Json.createObjectBuilder().add("patch", "test").build()
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
            container.stop();
        }
    }

    /**
     * RtIssue should be able to compare different instances.
     *
     */
    @Test
    public void canCompareInstances() {
        final RtIssue less = new RtIssue(new FakeRequest(), this.repo(), 1);
        final RtIssue greater = new RtIssue(new FakeRequest(), this.repo(), 2);
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * RtIssue can add a reaction.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void reacts() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Issue issue = new RtIssue(
                new ApacheRequest(container.home()),
                repo,
                10
            );
            issue.react(new Reaction.Simple(Reaction.HEART));
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Issue was unable to react",
                query.method(),
                new IsEqual<>(Request.POST)
            );
        }
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
