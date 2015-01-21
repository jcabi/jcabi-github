/**
 * Copyright (c) 2013-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.net.HttpURLConnection;
import java.util.List;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtUser}.
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public final class RtUserTest {
    /**
     * RtUser can understand who am I.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksWhoAmI() throws Exception {
        final String login = "monalia";
        final RtUser user = new RtUser(
            Mockito.mock(Github.class),
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("login", login)
                    .build().toString()
            )
        );
        MatcherAssert.assertThat(
            user.login(),
            Matchers.equalTo(login)
        );
    }

    /**
     * RtUser can check if he has a name.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksIfHeHasAName() throws Exception {
        final User.Smart smart = new User.Smart(
            new RtUser(
                Mockito.mock(Github.class),
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
            smart.hasName(),
            Matchers.equalTo(true)
        );
    }

    /**
     * RtUser can check if he has NO name.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksIfHeHasNoName() throws Exception {
        final User.Smart smart = new User.Smart(
            new RtUser(
                Mockito.mock(Github.class),
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .build()
                        .toString()
                ),
                "octoc"
            )
        );
        MatcherAssert.assertThat(
            smart.hasName(),
            Matchers.equalTo(false)
        );
    }

    /**
     * RtUser can describe as a JSON object.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void describeAsJson() throws Exception {
        final RtUser user = new RtUser(
            Mockito.mock(Github.class),
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
            user.json().toString(),
            Matchers.equalTo(
                "{\"name\":\"monalisa\",\"email\":\"octocat@github.com\"}"
            )
        );
    }

    /**
     * RtUser can execute PATCH request.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void executePatchRequest() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"login\":\"octocate\"}"
            )
        ).start();
        final RtUser json = new RtUser(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        json.patch(
            Json.createObjectBuilder()
                .add("location", "San Francisco")
                .build()
        );
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.PATCH)
        );
        container.stop();
    }

    /**
     * RtUser can fetch emails.
     */
    @Test
    public void fetchesEmails() {
        final Github github = Mockito.mock(Github.class);
        Mockito.when(github.entry()).thenReturn(new FakeRequest());
        final User user = new RtUser(github, new FakeRequest());
        MatcherAssert.assertThat(user.emails(), Matchers.notNullValue());
    }

    /**
     * RtUser can fetch organizations.
     */
    @Test
    public void fetchesOrganizations() {
        final Github github = Mockito.mock(Github.class);
        Mockito.when(github.entry()).thenReturn(new FakeRequest());
        final User user = new RtUser(github, new FakeRequest());
        MatcherAssert.assertThat(user.organizations(), Matchers.notNullValue());
    }

    /**
     * Tests if a User.Smart object handles gravatar_id JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasGravatar() throws Exception {
        final String value = "some-gravatar-id";
        final User.Smart smart = this.userWith("gravatar_id", value);
        MatcherAssert.assertThat(smart.gravatar(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles html_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasHtmlUrl() throws Exception {
        final String value = "http://github.example.com";
        final User.Smart smart = this.userWith("html_url", value);
        MatcherAssert.assertThat(smart.htmlUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles followers_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasFollwersUrl() throws Exception {
        final String value = "http://github.example.com/followers";
        final User.Smart smart = this.userWith("followers_url", value);
        MatcherAssert.assertThat(smart.follwersUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles following_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasFollowingUrl() throws Exception {
        final String value = "http://github.example.com/following";
        final User.Smart smart = this.userWith("following_url", value);
        MatcherAssert.assertThat(smart.followingUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles gists_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasGistsUrl() throws Exception {
        final String value = "http://github.example.com/gists";
        final User.Smart smart = this.userWith("gists_url", value);
        MatcherAssert.assertThat(smart.gistsUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles starred_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasStarredUrl() throws Exception {
        final String value = "http://github.example.com/starred";
        final User.Smart smart = this.userWith("starred_url", value);
        MatcherAssert.assertThat(smart.starredUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles subscriptions_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasSubscriptionsUrl() throws Exception {
        final String value = "http://github.example.com/subscriptions";
        final User.Smart smart = this.userWith("subscriptions_url", value);
        MatcherAssert.assertThat(smart.subscriptionsUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles organizations_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasOrganizationsUrl() throws Exception {
        final String value = "http://github.example.com/organizations";
        final User.Smart smart = this.userWith("organizations_url", value);
        MatcherAssert.assertThat(smart.organizationsUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles repos_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasReposUrl() throws Exception {
        final String value = "http://github.example.com/repos";
        final User.Smart smart = this.userWith("repos_url", value);
        MatcherAssert.assertThat(smart.reposUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles events_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasEventsUrl() throws Exception {
        final String value = "http://github.example.com/events";
        final User.Smart smart = this.userWith("events_url", value);
        MatcherAssert.assertThat(smart.eventsUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles received_events_url JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasReceivedEventsUrl() throws Exception {
        final String value = "http://github.example.com/received_events";
        final User.Smart smart = this.userWith("received_events_url", value);
        MatcherAssert.assertThat(smart.receivedEventsUrl(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles type JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasType() throws Exception {
        final String value = "http://github.example.com/organizations";
        final User.Smart smart = this.userWith("type", value);
        MatcherAssert.assertThat(smart.type(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles site_admin JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasSiteAdmin() throws Exception {
        final User.Smart smart = this.userWith("site_admin", "true");
        MatcherAssert.assertThat(smart.siteAdmin(), Matchers.is(true));
    }

    /**
     * Tests if a User.Smart object handles blog JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasBlog() throws Exception {
        final String value = "http://blog.example.com";
        final User.Smart smart = this.userWith("blog", value);
        MatcherAssert.assertThat(smart.blog(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles hireable JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasHireable() throws Exception {
        final User.Smart smart = this.userWith("hireable", "true");
        MatcherAssert.assertThat(smart.hireable(), Matchers.is(true));
    }

    /**
     * Tests if a User.Smart object handles bio JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasBio() throws Exception {
        final String value = "http://github.example.com/bio";
        final User.Smart smart = this.userWith("bio", value);
        MatcherAssert.assertThat(smart.bio(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles public_repos JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasPublicRepos() throws Exception {
        final int value = Tv.THREE;
        final User.Smart smart = this.userWith(
            "public_repos",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(smart.publicRepos(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles public_gists JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasPublicGists() throws Exception {
        final int value = Tv.FOUR;
        final User.Smart smart = this.userWith(
            "public_gists",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(smart.publicGists(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles followers JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasFollowersCount() throws Exception {
        final int value = Tv.FIVE;
        final User.Smart smart = this.userWith(
            "followers",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(smart.followersCount(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles following JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasFollowingCount() throws Exception {
        final int value = Tv.SIX;
        final User.Smart smart = this.userWith(
            "following",
            String.valueOf(value)
        );
        MatcherAssert.assertThat(smart.followingCount(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles created_at JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasCreated() throws Exception {
        final Github.Time value = new Github.Time("2014-07-04T15:29:43Z");
        final User.Smart smart = this.userWith("created_at", value.toString());
        MatcherAssert.assertThat(smart.created(), Matchers.is(value));
    }

    /**
     * Tests if a User.Smart object handles updated_at JSON property.
     * @throws Exception if any problem occurs.
     */
    @Test
    public void hasUpdated() throws Exception {
        final Github.Time value = new Github.Time("2014-07-04T15:29:43Z");
        final User.Smart smart = this.userWith("updated_at", value.toString());
        MatcherAssert.assertThat(smart.updated(), Matchers.is(value));
    }

    /**
     * Verifies the behaviour of the RtUser.notifications method.
     * @throws Exception
     */
    @Test
    public void notifications() throws Exception {
        final Github github = Mockito.mock(Github.class);
        final Request request = new FakeRequest().withBody("[{\"id\":\"55898574\",\"unread\":true,\"reason\":\"mention\",\"updated_at\":\"2015-01-21T14:14:12Z\",\"last_read_at\":null,\"subject\":{\"title\":\"DyTasksTest.java:81-82: Unignore following test when #492...\",\"url\":\"https://api.github.com/repos/yegor256/thindeck/issues/508\",\"latest_comment_url\":\"https://api.github.com/repos/yegor256/thindeck/issues/comments/70843546\",\"type\":\"Issue\"},\"repository\":{\"id\":17670227,\"name\":\"thindeck\",\"full_name\":\"yegor256/thindeck\",\"owner\":{\"login\":\"yegor256\",\"id\":526301,\"avatar_url\":\"https://avatars.githubusercontent.com/u/526301?v=3\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/yegor256\",\"html_url\":\"https://github.com/yegor256\",\"followers_url\":\"https://api.github.com/users/yegor256/followers\",\"following_url\":\"https://api.github.com/users/yegor256/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/yegor256/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/yegor256/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/yegor256/subscriptions\",\"organizations_url\":\"https://api.github.com/users/yegor256/orgs\",\"repos_url\":\"https://api.github.com/users/yegor256/repos\",\"events_url\":\"https://api.github.com/users/yegor256/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/yegor256/received_events\",\"type\":\"User\",\"site_admin\":false},\"private\":false,\"html_url\":\"https://github.com/yegor256/thindeck\",\"description\":\"Web Hosting That Deploys Itself\",\"fork\":false,\"url\":\"https://api.github.com/repos/yegor256/thindeck\",\"forks_url\":\"https://api.github.com/repos/yegor256/thindeck/forks\",\"keys_url\":\"https://api.github.com/repos/yegor256/thindeck/keys{/key_id}\",\"collaborators_url\":\"https://api.github.com/repos/yegor256/thindeck/collaborators{/collaborator}\",\"teams_url\":\"https://api.github.com/repos/yegor256/thindeck/teams\",\"hooks_url\":\"https://api.github.com/repos/yegor256/thindeck/hooks\",\"issue_events_url\":\"https://api.github.com/repos/yegor256/thindeck/issues/events{/number}\",\"events_url\":\"https://api.github.com/repos/yegor256/thindeck/events\",\"assignees_url\":\"https://api.github.com/repos/yegor256/thindeck/assignees{/user}\",\"branches_url\":\"https://api.github.com/repos/yegor256/thindeck/branches{/branch}\",\"tags_url\":\"https://api.github.com/repos/yegor256/thindeck/tags\",\"blobs_url\":\"https://api.github.com/repos/yegor256/thindeck/git/blobs{/sha}\",\"git_tags_url\":\"https://api.github.com/repos/yegor256/thindeck/git/tags{/sha}\",\"git_refs_url\":\"https://api.github.com/repos/yegor256/thindeck/git/refs{/sha}\",\"trees_url\":\"https://api.github.com/repos/yegor256/thindeck/git/trees{/sha}\",\"statuses_url\":\"https://api.github.com/repos/yegor256/thindeck/statuses/{sha}\",\"languages_url\":\"https://api.github.com/repos/yegor256/thindeck/languages\",\"stargazers_url\":\"https://api.github.com/repos/yegor256/thindeck/stargazers\",\"contributors_url\":\"https://api.github.com/repos/yegor256/thindeck/contributors\",\"subscribers_url\":\"https://api.github.com/repos/yegor256/thindeck/subscribers\",\"subscription_url\":\"https://api.github.com/repos/yegor256/thindeck/subscription\",\"commits_url\":\"https://api.github.com/repos/yegor256/thindeck/commits{/sha}\",\"git_commits_url\":\"https://api.github.com/repos/yegor256/thindeck/git/commits{/sha}\",\"comments_url\":\"https://api.github.com/repos/yegor256/thindeck/comments{/number}\",\"issue_comment_url\":\"https://api.github.com/repos/yegor256/thindeck/issues/comments/{number}\",\"contents_url\":\"https://api.github.com/repos/yegor256/thindeck/contents/{+path}\",\"compare_url\":\"https://api.github.com/repos/yegor256/thindeck/compare/{base}...{head}\",\"merges_url\":\"https://api.github.com/repos/yegor256/thindeck/merges\",\"archive_url\":\"https://api.github.com/repos/yegor256/thindeck/{archive_format}{/ref}\",\"downloads_url\":\"https://api.github.com/repos/yegor256/thindeck/downloads\",\"issues_url\":\"https://api.github.com/repos/yegor256/thindeck/issues{/number}\",\"pulls_url\":\"https://api.github.com/repos/yegor256/thindeck/pulls{/number}\",\"milestones_url\":\"https://api.github.com/repos/yegor256/thindeck/milestones{/number}\",\"notifications_url\":\"https://api.github.com/repos/yegor256/thindeck/notifications{?since,all,participating}\",\"labels_url\":\"https://api.github.com/repos/yegor256/thindeck/labels{/name}\",\"releases_url\":\"https://api.github.com/repos/yegor256/thindeck/releases{/id}\"},\"url\":\"https://api.github.com/notifications/threads/55898574\",\"subscription_url\":\"https://api.github.com/notifications/threads/55898574/subscription\"}]");
        Mockito.when(github.entry()).thenReturn(request);
        final RtUser user = new RtUser(github, request);
        final List<Notification> notifications = user.notifications();
        MatcherAssert.assertThat(
            notifications, Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            notifications.size(), Matchers.is(1)
        );
        MatcherAssert.assertThat(
            notifications.get(0).number(),
            Matchers.is(55898574L)
        );
    }

    /**
     * Return User.Smart with given property.
     * @param property The propery as specified at https://developer.github.com/v3/users/#get-a-single-user
     * @param value The property value
     * @return User.Smart with given property.
     */
    private User.Smart userWith(final String property, final String value) {
        return new User.Smart(
            new RtUser(
                Mockito.mock(Github.class),
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
