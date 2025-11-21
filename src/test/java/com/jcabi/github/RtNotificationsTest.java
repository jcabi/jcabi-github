/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
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
                    // @checkstyle LineLength (65 lines)
                    "[\n"
                    + "  {\n"
                    + "    \"id\": \"1\", \n"
                    + "    \"repository\": {\n"
                    + "      \"id\": 1296269, \n"
                    + "      \"owner\": {\n"
                    + "        \"login\": \"octocat\", \n"
                    + "        \"id\": 1, \n"
                    + "        \"avatar_url\": \"https://github.com/"
                    + "images/error/octocat_happy.gif\", \n"
                    + "        \"gravatar_id\": \"\", \n"
                    + "        \"url\": \"https://api.github.com/users/octocat\", \n"
                    + "        \"html_url\": \"https://github.com/octocat\", \n"
                    + "        \"followers_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "followers\", \n"
                    + "        \"following_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "following{/other_user}\", \n"
                    + "        \"gists_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "gists{/gist_id}\", \n"
                    + "        \"starred_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "starred{/owner}{/repo}\", \n"
                    + "        \"subscriptions_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "subscriptions\", \n"
                    + "        \"organizations_url\": "
                    + "\"https://api.github.com/users/octocat/orgs\", \n"
                    + "        \"repos_url\": \"https://api.github.com/users/octocat/repos\", \n"
                    + "        \"events_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "events{/privacy}\", \n"
                    + "        \"received_events_url\": "
                    + "\"https://api.github.com/users/octocat/"
                    + "received_events\", \n"
                    + "        \"type\": \"User\", \n"
                    + "        \"site_admin\": false\n"
                    + "      }, \n"
                    + "      \"name\": \"Hello-World\", \n"
                    + "      \"full_name\": \"octocat/Hello-World\", \n"
                    + "      \"description\": \"This your first repo!\", \n"
                    + "      \"private\": false, \n"
                    + "      \"fork\": false, \n"
                    + "      \"url\": \"https://api.github.com/repos/octocat/Hello-World\", \n"
                    + "      \"html_url\": \"https://github.com/octocat/Hello-World\"\n"
                    + "    }, \n"
                    + "    \"subject\": {\n"
                    + "      \"title\": \"Greetings\", \n"
                    + "      \"url\": "
                    + "\"https://api.github.com/repos/octokit/octokit.rb/issues/123\", \n"
                    + "      \"latest_comment_url\": "
                    + "\"https://api.github.com/repos/octokit/octokit.rb/issues/comments/123\", \n"
                    + "      \"type\": \"Issue\"\n"
                    + "    }, \n"
                    + "    \"reason\": \"subscribed\", \n"
                    + "    \"unread\": true, \n"
                    + "    \"updated_at\": \"2014-11-07T22:01:45Z\", \n"
                    + "    \"last_read_at\": \"2014-11-07T22:01:45Z\", \n"
                    + "    \"url\": \"https://api.github.com/notifications/threads/1\"\n"
                    + "  }\n]"
                )
            ).iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @Test
    @Disabled
    void markNotificationAsRead() {
        // Not implemented
    }
}
