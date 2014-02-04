/**
 * Copyright (c) 2012-2013, JCabi.com
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

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.immutable.ArrayMap;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssues}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
public final class RtIssuesTest {
    /**
     * RtIssues can create an issue.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void createIssue() throws Exception {
        final String title = "Found a bug";
        final String body = issue(title).toString();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body)).start();
        final RtIssues issues = new RtIssues(
            new JdkRequest(container.home()),
            repo()
        );
        final Issue issue = issues.create(title, "having a problem with it.");
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.POST)
        );
        MatcherAssert.assertThat(
            new Issue.Smart(issue).title(),
            Matchers.equalTo(title)
        );
        container.stop();
    }

    /**
     * RtIssues can get a single issue.
     * @throws Exception if some problem inside
     */
    @Test
    public void getSingleIssue() throws Exception {
        final String title = "Unit test";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                issue(title).toString()
            )
        ).start();
        final RtIssues issues = new RtIssues(
            new JdkRequest(container.home()),
            repo()
        );
        final Issue issue = issues.get(1);
        MatcherAssert.assertThat(
            new Issue.Smart(issue).title(),
            Matchers.equalTo(title)
        );
        container.stop();
    }

    /**
     * RtIssues can iterate issues.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateIssues() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(issue("new issue"))
                    .add(issue("code issue"))
                    .build().toString()
            )
        ).start();
        final RtIssues issues = new RtIssues(
            new JdkRequest(container.home()),
            repo()
        );
        MatcherAssert.assertThat(
            issues.iterate(new ArrayMap<String, String>()),
            Matchers.<Issue>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * Create and return JsonObject to test.
     * @param title The title of the issue
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject issue(final String title) throws Exception {
        return Json.createObjectBuilder()
            .add("number", 1)
            .add("state", Issue.OPEN_STATE)
            .add("title", title)
            .build();
    }

    /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
