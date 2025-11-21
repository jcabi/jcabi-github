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
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtComment}.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@ExtendWith(RandomPort.class)
final class RtCommentTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void canCompareInstances() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Issue issue = repo.issues().create("title", "body");
        final RtComment less = new RtComment(new FakeRequest(), issue, 1);
        final RtComment greater = new RtComment(new FakeRequest(), issue, 2);
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * RtComment can return its issue (owner).
     */
    @Test
    void returnsItsIssue() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Issue issue = repo.issues().create("testing1", "issue1");
        final RtComment comment = new RtComment(new FakeRequest(), issue, 1);
        MatcherAssert.assertThat(
            "Values are not equal", comment.issue(), Matchers.is(issue)
        );
    }

    @Test
    void returnsItsNumber() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Issue issue = repo.issues().create("testing2", "issue2");
        final long num = 10L;
        final RtComment comment = new RtComment(new FakeRequest(), issue, num);
        MatcherAssert.assertThat(
            "Values are not equal", comment.number(), Matchers.is(num)
        );
    }

    /**
     * This tests that the remove() method is working fine.
     */
    @Test
    void removesComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())) {
            final Repo repo = new MkGitHub().randomRepo();
            final Issue issue = repo.issues().create("testing3", "issue3");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            comment.remove();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void returnsItsJSon() throws IOException {
        final String body = "{\"body\":\"test5\"}";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body)
            ).start(RandomPort.port())) {
            final Repo repo = new MkGitHub().randomRepo();
            final Issue issue = repo.issues().create("testing4", "issue4");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            final JsonObject json = comment.json();
            MatcherAssert.assertThat(
                "Values are not equal",
                json.getString("body"),
                Matchers.is("test5")
            );
        }
    }

    @Test
    void patchesComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())) {
            final Repo repo = new MkGitHub().randomRepo();
            final Issue issue = repo.issues().create("testing5", "issue5");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            final JsonObject patch = Json.createObjectBuilder()
                .add("title", "test comment").build();
            comment.patch(patch);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(), Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void reacts() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())) {
            final Repo repo = new MkGitHub().randomRepo();
            final Issue issue = repo.issues().create(
                "Reaction adding test", "This is a test for adding a reaction"
            );
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            comment.react(new Reaction.Simple(Reaction.HEART));
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Assertion failed",
                query.method(),
                new IsEqual<>(Request.POST)
            );
        }
    }

    @Test
    void reactions() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                        .add("id", "1")
                        .add("content", "heart")
                        .build()
                    ).build().toString()
                )
            ).start(RandomPort.port())) {
            final Repo repo = new MkGitHub().randomRepo();
            final Issue issue = repo.issues().create(
                "Reaction Listing test", "This is a test for listing reactions"
            );
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            MatcherAssert.assertThat(
                "Assertion failed",
                comment.reactions(),
                new IsIterableWithSize<>(new IsEqual<>(1))
            );
        }
    }

    /**
     * This tests that the toString() method is working fine.
     */
    @Test
    void givesToString() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())) {
            final Repo repo = new MkGitHub().randomRepo();
            final Issue issue = repo.issues().create("testing6", "issue6");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            final String text = comment.toString();
            MatcherAssert.assertThat(
                "Values are not equal",
                text,
                Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                text,
                Matchers.endsWith("10")
            );
        }
    }
}
