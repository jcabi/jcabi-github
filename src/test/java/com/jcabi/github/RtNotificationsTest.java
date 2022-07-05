/**
 * Copyright (c) 2013-2022, jcabi.com
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

import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtNotifications}.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @todo #920 Create a test fetchSingleNotification and implement
 *  get() operation in RtNotifications.
 * @todo #920 Create a test fetchNonEmptyListOfNotifications and implement
 *  iterate() operation in RtNotifications.
 * @todo #920 Create a test markNotificationAsRead and implement
 *  mark() operation in RtNotifications.
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class RtNotificationsTest {

    /**
     * Method 'iterate()' returns empty iterable if the service responds with
     * no notifications.
     * @throws Exception if some problem inside
     */
    @Test
    public void iterateEmpty() throws Exception {
        MatcherAssert.assertThat(
            new RtNotifications(
                new FakeRequest()
                    .withBody("[]")
            ).iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * Method 'iterate()' will iterate over notifications sent by the service.
     * @throws Exception If some problem inside
     */
    @Test
    public void iterateNotifications() throws Exception {
        MatcherAssert.assertThat(
            new RtNotifications(
                new FakeRequest().withBody(
                    // @checkstyle StringLiteralsConcatenationCheck (50 lines)
                    // @checkstyle LineLength (50 lines)
                    "[\n"
                    + "  {\n"
                    + "    \"id\": \"1\",\n"
                    + "    \"repository\": {\n"
                    + "      \"id\": 1296269,\n"
                    + "      \"owner\": {\n"
                    + "        \"login\": \"octocat\",\n"
                    + "        \"id\": 1,\n"
                    + "        \"avatar_url\": \"https://github.com/images/error/octocat_happy.gif\",\n"
                    + "        \"gravatar_id\": \"\",\n"
                    + "        \"url\": \"https://api.github.com/users/octocat\",\n"
                    + "        \"html_url\": \"https://github.com/octocat\",\n"
                    + "        \"followers_url\": \"https://api.github.com/users/octocat/followers\",\n"
                    + "        \"following_url\": \"https://api.github.com/users/octocat/following{/other_user}\",\n"
                    + "        \"gists_url\": \"https://api.github.com/users/octocat/gists{/gist_id}\",\n"
                    + "        \"starred_url\": \"https://api.github.com/users/octocat/starred{/owner}{/repo}\",\n"
                    + "        \"subscriptions_url\": \"https://api.github.com/users/octocat/subscriptions\",\n"
                    + "        \"organizations_url\": \"https://api.github.com/users/octocat/orgs\",\n"
                    + "        \"repos_url\": \"https://api.github.com/users/octocat/repos\",\n"
                    + "        \"events_url\": \"https://api.github.com/users/octocat/events{/privacy}\",\n"
                    + "        \"received_events_url\": \"https://api.github.com/users/octocat/received_events\",\n"
                    + "        \"type\": \"User\",\n"
                    + "        \"site_admin\": false\n"
                    + "      },\n"
                    + "      \"name\": \"Hello-World\",\n"
                    + "      \"full_name\": \"octocat/Hello-World\",\n"
                    + "      \"description\": \"This your first repo!\",\n"
                    + "      \"private\": false,\n"
                    + "      \"fork\": false,\n"
                    + "      \"url\": \"https://api.github.com/repos/octocat/Hello-World\",\n"
                    + "      \"html_url\": \"https://github.com/octocat/Hello-World\"\n"
                    + "    },\n"
                    + "    \"subject\": {\n"
                    + "      \"title\": \"Greetings\",\n"
                    + "      \"url\": \"https://api.github.com/repos/octokit/octokit.rb/issues/123\",\n"
                    + "      \"latest_comment_url\": \"https://api.github.com/repos/octokit/octokit.rb/issues/comments/123\",\n"
                    + "      \"type\": \"Issue\"\n"
                    + "    },\n"
                    + "    \"reason\": \"subscribed\",\n"
                    + "    \"unread\": true,\n"
                    + "    \"updated_at\": \"2014-11-07T22:01:45Z\",\n"
                    + "    \"last_read_at\": \"2014-11-07T22:01:45Z\",\n"
                    + "    \"url\": \"https://api.github.com/notifications/threads/1\"\n"
                    + "  }\n"
                    + "]"
                )
            ).iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * RtNotifications can mark Notification as read.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void markNotificationAsRead() throws Exception  {
        // Not implemented
    }
}
