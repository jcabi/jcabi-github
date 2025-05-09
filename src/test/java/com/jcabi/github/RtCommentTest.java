/**
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
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtComment}.
 *
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RtCommentTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtComment should be able to compare different instances.
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue issue = repo.issues().create("title", "body");
        final RtComment less = new RtComment(new FakeRequest(), issue, 1);
        final RtComment greater = new RtComment(new FakeRequest(), issue, 2);
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * RtComment can return its issue (owner).
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void returnsItsIssue() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue issue = repo.issues().create("testing1", "issue1");
        final RtComment comment = new RtComment(new FakeRequest(), issue, 1);
        MatcherAssert.assertThat(comment.issue(), Matchers.is(issue));
    }

    /**
     * RtComment can return its number.
     * @throws Exception - in case something goes wrong.
     */
    @Test
    public void returnsItsNumber() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue issue = repo.issues().create("testing2", "issue2");
        final long num = 10L;
        final RtComment comment = new RtComment(new FakeRequest(), issue, num);
        MatcherAssert.assertThat(comment.number(), Matchers.is(num));
    }

    /**
     * This tests that the remove() method is working fine.
     * @throws Exception - in case something goes wrong.
     */
    @Test
    public void removesComment() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Issue issue = repo.issues().create("testing3", "issue3");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            comment.remove();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    /**
     * RtComment can return its JSon description.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void returnsItsJSon() throws Exception {
        final String body = "{\"body\":\"test5\"}";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body)
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Issue issue = repo.issues().create("testing4", "issue4");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            final JsonObject json = comment.json();
            MatcherAssert.assertThat(
                json.getString("body"),
                Matchers.is("test5")
            );
        }
    }

    /**
     * RtComment can patch a comment.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void patchesComment() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Issue issue = repo.issues().create("testing5", "issue5");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            final JsonObject jsonPatch = Json.createObjectBuilder()
                .add("title", "test comment").build();
            comment.patch(jsonPatch);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(), Matchers.equalTo(Request.PATCH)
            );
        }
    }

    /**
     * RtComment can add a reaction.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void reacts() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Issue issue = repo.issues().create(
                "Reaction adding test", "This is a test for adding a reaction"
            );
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            comment.react(new Reaction.Simple(Reaction.HEART));
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                new IsEqual<>(Request.POST)
            );
        }
    }

    /**
     * RtComment can list its reactions.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void reactions() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
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
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Issue issue = repo.issues().create(
                "Reaction Listing test", "This is a test for listing reactions"
            );
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            MatcherAssert.assertThat(
                comment.reactions(),
                new IsIterableWithSize<>(new IsEqual<>(1))
            );
        }
    }

    /**
     * This tests that the toString() method is working fine.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void givesToString() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(this.resource.port())) {
            final Repo repo = new MkGithub().randomRepo();
            final Issue issue = repo.issues().create("testing6", "issue6");
            final RtComment comment = new RtComment(
                new ApacheRequest(container.home()), issue, 10
            );
            final String stringComment = comment.toString();
            MatcherAssert.assertThat(
                stringComment,
                Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
            );
            MatcherAssert.assertThat(stringComment, Matchers.endsWith("10"));
        }
    }
}
