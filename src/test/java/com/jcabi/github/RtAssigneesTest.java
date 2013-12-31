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

import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtAssignees}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtAssigneesTest {

    /**
     * RtAssignees can iterate over assignees.
     * @throws Exception Exception If some problem inside
     */
    @Test
    @Ignore
    public void iteratesAssignees() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(json("octocat"))
                    .add(json("dummy"))
                    .build().toString()
            )
        ).start();
        final Assignees users = new RtAssignees(
            repo(),
            new JdkRequest(container.home())
        );
        MatcherAssert.assertThat(
            users.iterate(),
            Matchers.<User>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * RtAssignees can check if user is assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    @Ignore
    public void checkUserIsAssigneeForRepo() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(json("octocat"))
                    .add(json("dummy"))
                    .build().toString()
            )
        ).start();
        final Assignees users = new RtAssignees(
            repo(),
            new JdkRequest(container.home())
        );
        MatcherAssert.assertThat(
            users.check("octocat"),
            Matchers.equalTo(true)
        );
        MatcherAssert.assertThat(
            users.check("octocat52"),
            Matchers.equalTo(false)
        );
        container.stop();
    }

    /**
     * Create and return JsonObject to test.
     * @param login Username to login
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject json(final String login) throws Exception {
        return Json.createObjectBuilder()
            .add("login", login)
            .build();
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "assignee"))
            .when(repo).coordinates();
        return repo;
    }
}
