/**
 * Copyright (c) 2013-2024, jcabi.com
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

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtUsers}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
public final class RtUsersTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtUsers can iterate users.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateUsers() throws Exception {
        final String identifier = "1";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(RtUsersTest.json("octocat", identifier))
                    .add(RtUsersTest.json("dummy", "2"))
                    .build().toString()
            )
        ).start(this.resource.port());
        final Users users = new RtUsers(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            users.iterate(identifier),
            Matchers.<User>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * RtUsers can get a single user.
     *
     * @throws Exception  if there is any error
     */
    @Test
    public void getSingleUser() throws Exception {
        final String login = "mark";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                RtUsersTest.json(login, "3").toString()
            )
        ).start(this.resource.port());
        final Users users = new RtUsers(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            users.get(login).login(),
            Matchers.equalTo(login)
        );
        container.stop();
    }

    /**
     * RtUsers can get a current  user.
     *
     * @throws Exception  if there is any error
     */
    @Test
    public void getCurrentUser() throws Exception {
        final String login = "kendy";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                RtUsersTest.json(login, "4").toString()
            )
        ).start(this.resource.port());
        final Users users = new RtUsers(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            users.self().login(),
            Matchers.equalTo(login)
        );
        container.stop();
    }

    /**
     * Create and return JsonObject to test.
     * @param login Username to login
     * @param identifier User Id
     * @return JsonObject
     */
    private static JsonObject json(
        final String login, final String identifier
    ) {
        return Json.createObjectBuilder()
            .add("id", Integer.valueOf(identifier))
            .add("login", login)
            .build();
    }
}
