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

import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.request.ApacheRequest;
import com.rexsl.test.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtUser}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
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
}
