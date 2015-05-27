/**
 * Copyright (c) 2013-2015, jcabi.com
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
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for RtCommits.
 *
 * @author Marcin Cylke (marcin.cylke+github@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public class RtStatusesTest {

    /**
     * Tests creating a Commit.
     *
     * @throws Exception when an Error occurs
     */
    @Test
    public final void createsStatus() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().add("state", "failure")
                    .add("target_url", "https://ci.example.com/1000/output")
                    .add("description", "Build has completed successfully")
                    .add("context", "continuous-integration/jenkins")
                    .build().toString()
            )
        ).start();
        final Request req = new ApacheRequest(container.home());
        final Statuses statuses = new RtStatuses(
            req, new RtCommit(req, repo(), "0abcd89jcabitest")
        );
        try {
            statuses.create(
                new RtStatus(
                    Status.State.Failure, "http://example.com",
                    "description", "ctx"
                )
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            Matchers.equalToIgnoringCase(Status.State.Failure.name());
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
            new Repos.RepoCreate("test", false)
        );
    }
}
