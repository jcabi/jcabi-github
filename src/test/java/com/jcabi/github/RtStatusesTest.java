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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import java.net.HttpURLConnection;

/**
 * Testcase for RtCommits.
 *
 * @author Marcin Cylke
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class RtStatusesTest {

    /**
     * Tests creating a Commit.
     *
     * @throws Exception when an error occurs
     */
    @Test
    public final void createsStatus() throws Exception {
        final String sha = "0abcd89jcabitest";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\n" +
                        "  \"created_at\": \"2012-07-20T01:19:13Z\",\n" +
                        "  \"updated_at\": \"2012-07-20T01:19:13Z\",\n" +
                        "  \"state\": \"failure\",\n" +
                        "  \"target_url\": \"https://ci.example.com/1000/output\",\n" +
                        "  \"description\": \"Build has completed successfully\",\n" +
                        "  \"id\": 1,\n" +
                        "  \"url\": \"https://api.github.com/repos/octocat/Hello-World/statuses/1\",\n" +
                        "  \"context\": \"continuous-integration/jenkins\",\n" +
                        "  \"creator\": {\n" +
                        "    \"login\": \"octocat\",\n" +
                        "    \"id\": 1,\n" +
                        "    \"avatar_url\": \"https://github.com/images/error/octocat_happy.gif\",\n" +
                        "    \"gravatar_id\": \"\",\n" +
                        "    \"url\": \"https://api.github.com/users/octocat\",\n" +
                        "    \"html_url\": \"https://github.com/octocat\",\n" +
                        "    \"followers_url\": \"https://api.github.com/users/octocat/followers\",\n" +
                        "    \"following_url\": \"https://api.github.com/users/octocat/following{/other_user}\",\n" +
                        "    \"gists_url\": \"https://api.github.com/users/octocat/gists{/gist_id}\",\n" +
                        "    \"starred_url\": \"https://api.github.com/users/octocat/starred{/owner}{/repo}\",\n" +
                        "    \"subscriptions_url\": \"https://api.github.com/users/octocat/subscriptions\",\n" +
                        "    \"organizations_url\": \"https://api.github.com/users/octocat/orgs\",\n" +
                        "    \"repos_url\": \"https://api.github.com/users/octocat/repos\",\n" +
                        "    \"events_url\": \"https://api.github.com/users/octocat/events{/privacy}\",\n" +
                        "    \"received_events_url\": \"https://api.github.com/users/octocat/received_events\",\n" +
                        "    \"type\": \"User\",\n" +
                        "    \"site_admin\": false\n" +
                        "  }\n" +
                        "}"
            )
        ).start();
        final Request req = new ApacheRequest(container.home());
        final Statuses statuses = new RtStatuses(req, new RtCommit(req, repo(), sha));

        try {
            final Status newStatus = statuses.create(
                    new RtStatus(StatusState.failure, "http://example.com", "description", "ctx"));
            MatcherAssert.assertThat(
                    newStatus,
                Matchers.instanceOf(Status.class)
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                newStatus.state().name(),
                Matchers.equalTo(StatusState.failure.name())
            );
        } finally {
            container.stop();
        }
    }

    /**
     * This method returns a Repo for testing.
     *
     * @return Repo - a repo to be used for test.
     * @throws Exception - if anything goes wrong.
     */
    private static Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }
}
