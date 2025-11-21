/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
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
import java.text.ParseException;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtUser}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle LineLengthCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle MethodNameCheck (500 lines)
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public final class RtUserTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void checksWhoAmI() throws IOException {
        final String login = "monalia";
        final RtUser user = new RtUser(
            Mockito.mock(GitHub.class),
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("login", login)
                    .build().toString()
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            user.login(),
            Matchers.equalTo(login)
        );
    }

    @Test
    public void checksIfHeHasAName() throws IOException {
        final User.Smart smart = new User.Smart(
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
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.hasName(),
            Matchers.equalTo(true)
        );
    }

    @Test
    public void checksIfHeHasNoName() throws IOException {
        final User.Smart smart = new User.Smart(
            new RtUser(
                Mockito.mock(GitHub.class),
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .build()
                        .toString()
                ),
                "octoc"
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.hasName(),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void describeAsJson() throws IOException {
        final RtUser user = new RtUser(
            Mockito.mock(GitHub.class),
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("name", "monalisa")
                    .add("email", "octocat@github.com")
                    .build()
                    .toString()
            ),
            "octoc"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            user.json().toString(),
            Matchers.equalTo(
                "{\"name\":\"monalisa\", \"email\":\"octocat@github.com\"}"
            )
        );
    }

    @Test
    public void executePatchRequest() throws IOException {
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
    public void fetchesEmails() {
        final GitHub github = Mockito.mock(GitHub.class);
        Mockito.when(github.entry()).thenReturn(new FakeRequest());
        final User user = new RtUser(github, new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null", user.emails(), Matchers.notNullValue()
        );
    }

    @Test
    public void fetchesOrganizations() {
        final GitHub github = Mockito.mock(GitHub.class);
        Mockito.when(github.entry()).thenReturn(new FakeRequest());
        final User user = new RtUser(github, new FakeRequest());
        MatcherAssert.assertThat(
            "Value is null", user.organizations(), Matchers.notNullValue()
        );
    }

    @Test
    public void hasHtmlUrl() throws IOException {
        final String value = "http://github.example.com";
        final User.Smart smart = RtUserTest.userWith("html_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.htmlUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasFollwersUrl() throws IOException {
        final String value = "http://github.example.com/followers";
        final User.Smart smart = RtUserTest.userWith("followers_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.followersUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasFollowingUrl() throws IOException {
        final String value = "http://github.example.com/following";
        final User.Smart smart = RtUserTest.userWith("following_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.followingUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasGistsUrl() throws IOException {
        final String value = "http://github.example.com/gists";
        final User.Smart smart = RtUserTest.userWith("gists_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.gistsUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasStarredUrl() throws IOException {
        final String value = "http://github.example.com/starred";
        final User.Smart smart = RtUserTest.userWith("starred_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.starredUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasSubscriptionsUrl() throws IOException {
        final String value = "http://github.example.com/subscriptions";
        final User.Smart smart = RtUserTest.userWith("subscriptions_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.subscriptionsUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasOrganizationsUrl() throws IOException {
        final String value = "http://github.example.com/organizations";
        final User.Smart smart = RtUserTest.userWith("organizations_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.organizationsUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasReposUrl() throws IOException {
        final String value = "http://github.example.com/repos";
        final User.Smart smart = RtUserTest.userWith("repos_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.reposUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasEventsUrl() throws IOException {
        final String value = "http://github.example.com/events";
        final User.Smart smart = RtUserTest.userWith("events_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.eventsUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasReceivedEventsUrl() throws IOException {
        final String value = "http://github.example.com/received_events";
        final User.Smart smart = RtUserTest.userWith("received_events_url", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.receivedEventsUrl(), Matchers.is(value)
        );
    }

    @Test
    public void hasType() throws IOException {
        final String value = "http://github.example.com/organizations";
        final User.Smart smart = RtUserTest.userWith("type", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.type(), Matchers.is(value)
        );
    }

    @Test
    public void hasSiteAdmin() throws IOException {
        final User.Smart smart = RtUserTest.userWith("site_admin", "true");
        MatcherAssert.assertThat(
            "Values are not equal", smart.siteAdmin(), Matchers.is(true)
        );
    }

    @Test
    public void hasBlog() throws IOException {
        final String value = "http://blog.example.com";
        final User.Smart smart = RtUserTest.userWith("blog", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.blog(), Matchers.is(value)
        );
    }

    @Test
    public void hasHireable() throws IOException {
        final User.Smart smart = RtUserTest.userWith("hireable", "true");
        MatcherAssert.assertThat(
            "Values are not equal", smart.hireable(), Matchers.is(true)
        );
    }

    @Test
    public void hasBio() throws IOException {
        final String value = "http://github.example.com/bio";
        final User.Smart smart = RtUserTest.userWith("bio", value);
        MatcherAssert.assertThat(
            "Values are not equal", smart.bio(), Matchers.is(value)
        );
    }

    @Test
    public void hasPublicRepos() throws IOException {
        final int value = Tv.THREE;
        final User.Smart smart = RtUserTest.userWith(
            "public_repos",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(
            "Values are not equal", smart.publicRepos(), Matchers.is(value)
        );
    }

    @Test
    public void hasPublicGists() throws IOException {
        final int value = Tv.FOUR;
        final User.Smart smart = RtUserTest.userWith(
            "public_gists",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(
            "Values are not equal", smart.publicGists(), Matchers.is(value)
        );
    }

    @Test
    public void hasFollowersCount() throws IOException {
        final int value = Tv.FIVE;
        final User.Smart smart = RtUserTest.userWith(
            "followers",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(
            "Values are not equal", smart.followersCount(), Matchers.is(value)
        );
    }

    @Test
    public void hasFollowingCount() throws IOException {
        final int value = Tv.SIX;
        final User.Smart smart = RtUserTest.userWith(
            "following",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(
            "Values are not equal", smart.followingCount(), Matchers.is(value)
        );
    }

    @Test
    public void hasCreated() throws ParseException, IOException {
        final GitHub.Time value = new GitHub.Time("2014-07-04T15:29:43Z");
        final User.Smart smart = RtUserTest.userWith("created_at", value.toString());
        MatcherAssert.assertThat(
            "Values are not equal", smart.created(), Matchers.is(value)
        );
    }

    @Test
    public void hasUpdated() throws ParseException, IOException {
        final GitHub.Time value = new GitHub.Time("2014-07-04T15:29:43Z");
        final User.Smart smart = RtUserTest.userWith("updated_at", value.toString());
        MatcherAssert.assertThat(
            "Values are not equal", smart.updated(), Matchers.is(value)
        );
    }

    @Test
    public void notifications() throws IOException {
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
    public void markAsReadOkIfResponseStatusIs205() throws IOException {
        MkContainer container = null;
        try {
            container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_RESET)
            ).start(RandomPort.port());
            final Request req = new ApacheRequest(container.home());
            final GitHub github = Mockito.mock(GitHub.class);
            Mockito.when(github.entry()).thenReturn(req);
            new RtUser(
                github,
                req
            ).markAsRead(new Date());
        } finally {
            container.close();
        }
    }

    /**
     * Method 'markAsRead()' should fail if response code is not 205.
     */
    @Test(expected = AssertionError.class)
    public void markAsReadErrorIfResponseStatusIsNot205() throws IOException {
        MkContainer container = null;
        try {
            container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
            ).start(RandomPort.port());
            final Request req = new ApacheRequest(container.home());
            final GitHub github = Mockito.mock(GitHub.class);
            Mockito.when(github.entry()).thenReturn(req);
            new RtUser(
                github,
                req
            ).markAsRead(new Date());
        } finally {
            container.close();
        }
    }

    /**
     * Return User.Smart with given property.
     * @param property The propery as specified at https://developer.github.com/v3/users/#get-a-single-user
     * @param value The property value
     * @return User.Smart with given property.
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
