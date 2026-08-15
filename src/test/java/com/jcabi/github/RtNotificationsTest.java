/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtNotifications}.
 * @since 0.1
 * @todo #920 Create a test fetchSingleNotification and implement
 *  get() operation in RtNotifications.
 * @todo #920 Create a test fetchNonEmptyListOfNotifications and implement
 *  iterate() operation in RtNotifications.
 * @todo #920 Create a test markNotificationAsRead and implement
 *  mark() operation in RtNotifications.
 */
final class RtNotificationsTest {

    /**
     * Method 'iterate()' returns empty iterable if the service responds with
     * no notifications.
     */
    @Test
    void iterateEmpty() {
        MatcherAssert.assertThat(
            "Collection is not empty",
            new RtNotifications(
                new FakeRequest()
                    .withBody("[]")
            ).iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * Method 'iterate()' will iterate over notifications sent by the service.
     */
    @Test
    void iterateNotifications() {
        MatcherAssert.assertThat(
            "Assertion failed",
            new RtNotifications(
                new FakeRequest().withBody(
                    // @checkstyle StringLiteralsConcatenationCheck (65 lines)
                    "["
                    + "  {"
                    + "    \"id\": \"1\","
                    + "    \"repository\": {"
                    + "      \"id\": 1296269,"
                    + "      \"owner\": {"
                    + "        \"login\": \"octocat\","
                    + "        \"id\": 1,"
                    + "        \"avatar_url\": \"https://github.com/"
                    + "images/error/octocat_happy.gif\","
                    + "        \"gravatar_id\": \"\","
                    + "        \"url\": \"https://api.github.com/users/octocat\","
                    + "        \"html_url\": \"https://github.com/octocat\","
                    + "        \"followers_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "followers\","
                    + "        \"following_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "following{/other_user}\","
                    + "        \"gists_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "gists{/gist_id}\","
                    + "        \"starred_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "starred{/owner}{/repo}\","
                    + "        \"subscriptions_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "subscriptions\","
                    + "        \"organizations_url\": "
                    + "\"https://api.github.com/users/octocat/orgs\","
                    + "        \"repos_url\": \"https://api.github.com/users/octocat/repos\","
                    + "        \"events_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "events{/privacy}\","
                    + "        \"received_events_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "received_events\","
                    + "        \"type\": \"User\","
                    + "        \"site_admin\": false"
                    + "      },"
                    + "      \"name\": \"Hello-World\","
                    + "      \"full_name\": \"octocat/Hello-World\","
                    + "      \"description\": \"This your first repo!\","
                    + "      \"private\": false,"
                    + "      \"fork\": false,"
                    + "      \"url\": \"https://api.github.com/repos/octocat/Hello-World\","
                    + "      \"html_url\": \"https://github.com/octocat/Hello-World\""
                    + "    },"
                    + "    \"subject\": {"
                    + "      \"title\": \"Greetings\","
                    + "      \"url\": "
                    + "\"https://api.github.com/repos/octokit/octokit.rb/issues/123\","
                    + "      \"latest_comment_url\": "
                    + "\"https://api.github.com/repos/octokit/octokit.rb/issues/comments/123\","
                    + "      \"type\": \"Issue\""
                    + "    },"
                    + "    \"reason\": \"subscribed\","
                    + "    \"unread\": true,"
                    + "    \"updated_at\": \"2014-11-07T22:01:45Z\","
                    + "    \"last_read_at\": \"2014-11-07T22:01:45Z\","
                    + "    \"url\": \"https://api.github.com/notifications/threads/1\""
                    + "  }]"
                )
            ).iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @Test
    @Disabled
    void markNotificationAsRead() {
        Assertions.fail("Marking of a notification as read is not tested yet");
    }
}
