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
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Instant;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtUser}.
 * @since 0.1
 * @checkstyle MethodNameCheck (500 lines)
 */
@ExtendWith(RandomPort.class)
final class RtUserTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void checksWhoAmI() throws IOException {
        final String login = "monalia";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtUser(
                Mockito.mock(GitHub.class),
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("login", login)
                        .build().toString()
                )
            ).login(),
            Matchers.equalTo(login)
        );
    }

    @Test
    void checksIfHeHasAName() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new User.Smart(
                new RtUser(
                    Mockito.mock(GitHub.class),
                    new FakeRequest().withBody(
                        Json.createObjectBuilder()
                            .add("name", "octoc")
                            .build()
                            .toString()
                    ),
                    "octoc"
                )
            ).hasName(),
            Matchers.equalTo(true)
        );
    }

    @Test
    void checksIfHeHasNoName() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new User.Smart(
                new RtUser(
                    Mockito.mock(GitHub.class),
                    new FakeRequest().withBody(
                        Json.createObjectBuilder()
                            .build()
                            .toString()
                    ),
                    "octoc"
                )
            ).hasName(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void hasNameReturnsFalseWhenNameIsNull() throws IOException {
        MatcherAssert.assertThat(
            "hasName() must return false when 'name' JSON value is null",
            RtUserTest.userWithNullName().hasName(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void nameThrowsIllegalStateWhenNameIsNull() throws IOException {
        Assertions.assertThrows(
            IllegalStateException.class,
            RtUserTest.userWithNullName()::name,
            "name() must throw IllegalStateException when 'name' JSON value is null"
        );
    }

    @Test
    void describeAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtUser(
                Mockito.mock(GitHub.class),
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("name", "monalisa")
                        .add("email", "octocat@github.com")
                        .build()
                        .toString()
                ),
                "octoc"
            ).json().toString(),
            Matchers.equalTo(
                "{\"name\":\"monalisa\",\"email\":\"octocat@github.com\"}"
            )
        );
    }

    @Test
    void executePatchRequest() throws IOException {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"login\":\"octocate\"}"
            )
        ).start(RandomPort.port());
        final RtUser json = new RtUser(
            Mockito.mock(GitHub.class),
            new ApacheRequest(container.home())
        );
        json.patch(
            Json.createObjectBuilder()
                .add("location", "San Francisco")
                .build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            container.take().method(),
            Matchers.equalTo(Request.PATCH)
        );
        container.stop();
    }

    @Test
    void fetchesEmails() {
        final GitHub github = Mockito.mock(GitHub.class);
        Mockito.when(github.entry()).thenReturn(new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null", new RtUser(github, new FakeRequest()).emails(), Matchers.notNullValue()
        );
    }

    @Test
    void fetchesOrganizations() {
        final GitHub github = Mockito.mock(GitHub.class);
        Mockito.when(github.entry()).thenReturn(new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null",
            new RtUser(github, new FakeRequest()).organizations(),
            Matchers.notNullValue()
        );
    }

    @Test
    void hasHtmlUrl() throws IOException {
        final String value = "http://github.example.com";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("html_url", value).htmlUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasFollowersUrl() throws IOException {
        final String value = "http://github.example.com/followers";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("followers_url", value).followersUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasFollowingUrl() throws IOException {
        final String value = "http://github.example.com/following";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("following_url", value).followingUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasGistsUrl() throws IOException {
        final String value = "http://github.example.com/gists";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("gists_url", value).gistsUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasStarredUrl() throws IOException {
        final String value = "http://github.example.com/starred";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("starred_url", value).starredUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasSubscriptionsUrl() throws IOException {
        final String value = "http://github.example.com/subscriptions";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("subscriptions_url", value).subscriptionsUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasOrganizationsUrl() throws IOException {
        final String value = "http://github.example.com/organizations";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("organizations_url", value).organizationsUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasReposUrl() throws IOException {
        final String value = "http://github.example.com/repos";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("repos_url", value).reposUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasEventsUrl() throws IOException {
        final String value = "http://github.example.com/events";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("events_url", value).eventsUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasReceivedEventsUrl() throws IOException {
        final String value = "http://github.example.com/received_events";
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("received_events_url", value).receivedEventsUrl(),
            Matchers.is(value)
        );
    }

    @Test
    void hasType() throws IOException {
        final String value = "http://github.example.com/organizations";
        MatcherAssert.assertThat(
            "Values are not equal", RtUserTest.userWith("type", value).type(), Matchers.is(value)
        );
    }

    @Test
    void hasSiteAdmin() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("site_admin", "true").siteAdmin(),
            Matchers.is(true)
        );
    }

    @Test
    void hasBlog() throws IOException {
        final String value = "http://blog.example.com";
        MatcherAssert.assertThat(
            "Values are not equal", RtUserTest.userWith("blog", value).blog(), Matchers.is(value)
        );
    }

    @Test
    void hasHireable() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("hireable", "true").hireable(),
            Matchers.is(true)
        );
    }

    @Test
    void hasBio() throws IOException {
        final String value = "http://github.example.com/bio";
        MatcherAssert.assertThat(
            "Values are not equal", RtUserTest.userWith("bio", value).bio(), Matchers.is(value)
        );
    }

    @Test
    void hasPublicRepos() throws IOException {
        final int value = 3;
        MatcherAssert.assertThat(
            "Values are not equal", RtUserTest.userWith(
                "public_repos",
                String.valueOf(value)
            ).publicRepos(), Matchers.is(value)
        );
    }

    @Test
    void hasPublicGists() throws IOException {
        final int value = 4;
        MatcherAssert.assertThat(
            "Values are not equal", RtUserTest.userWith(
                "public_gists",
                String.valueOf(value)
            ).publicGists(), Matchers.is(value)
        );
    }

    @Test
    void hasFollowersCount() throws IOException {
        final int value = 5;
        MatcherAssert.assertThat(
            "Values are not equal", RtUserTest.userWith(
                "followers",
                String.valueOf(value)
            ).followersCount(), Matchers.is(value)
        );
    }

    @Test
    void hasFollowingCount() throws IOException {
        final int value = 6;
        MatcherAssert.assertThat(
            "Values are not equal", RtUserTest.userWith(
                "following",
                String.valueOf(value)
            ).followingCount(), Matchers.is(value)
        );
    }

    @Test
    void hasCreated() throws IOException {
        final GitHub.Time value = new GitHub.Time("2014-07-04T15:29:43Z");
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("created_at", value.toString())
                .created().toString(),
            Matchers.is(value.toString())
        );
    }

    @Test
    void hasUpdated() throws IOException {
        final GitHub.Time value = new GitHub.Time("2014-07-04T15:29:43Z");
        MatcherAssert.assertThat(
            "Values are not equal",
            RtUserTest.userWith("updated_at", value.toString())
                .updated().toString(),
            Matchers.is(value.toString())
        );
    }

    @Test
    void notifications() throws IOException {
        MatcherAssert.assertThat(
            "Value is not null",
            new RtUser(
                new MkGitHub(),
                new FakeRequest()
            ).notifications(),
            Matchers.not(Matchers.nullValue())
        );
    }

    /**
     * Method 'markAsRead()' should complete successfully if response code is
     * 205.
     */
    @Test
    void markAsReadOkIfResponseStatusIs205() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_RESET)
            ).start(RandomPort.port())
        ) {
            final Request req = new ApacheRequest(container.home());
            final GitHub github = Mockito.mock(GitHub.class);
            Mockito.when(github.entry()).thenReturn(req);
            Assertions.assertDoesNotThrow(
                () -> new RtUser(github, req).markAsRead(Instant.now()),
                "Notifications are not marked as read"
            );
        }
    }

    @Test
    void markAsReadErrorIfResponseStatusIsNot205() throws IOException {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
        ).start(RandomPort.port());
        try {
            final Request req = new ApacheRequest(container.home());
            final GitHub github = Mockito.mock(GitHub.class);
            Mockito.when(github.entry()).thenReturn(req);
            final RtUser user = new RtUser(github, req);
            Assertions.assertThrows(
                AssertionError.class,
                () -> user.markAsRead(Instant.now()),
                "Should throw when response status is not 205"
            );
        } finally {
            container.close();
        }
    }

    /**
     * Return User.Smart with a JSON null "name" property.
     * @return User.Smart whose JSON has "name":null
     */
    private static User.Smart userWithNullName() {
        return new User.Smart(
            new RtUser(
                Mockito.mock(GitHub.class),
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .addNull("name")
                        .build()
                        .toString()
                ),
                "octoc"
            )
        );
    }

    /**
     * Return User.Smart with given property.
     * @param property The property as specified at https://developer.github.com/v3/users/#get-a-single-user
     * @param value The property value
     * @return User.Smart with given property
     */
    private static User.Smart userWith(final String property, final String value) {
        return new User.Smart(
            new RtUser(
                Mockito.mock(GitHub.class),
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add(property, value)
                        .build()
                        .toString()
                ),
                "octoc"
            )
        );
    }
}
