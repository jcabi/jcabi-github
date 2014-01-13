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
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.ApacheRequest;
import com.rexsl.test.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtOrganization}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtOrganizationTest {

    /**
     * RtOrganization should be able to describe itself in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canFetchIssueAsJson() throws Exception {
        final RtOrganization org = new RtOrganization(
            new FakeRequest().withBody("{\"organization\":\"json\"}"),
            this.user(),
            1
        );
        MatcherAssert.assertThat(
            org.json().getString("organization"),
            Matchers.equalTo("json")
        );
    }

    /**
     * RtOrganization should be able to perform a patch request.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void patchWithJson() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
        ).start();
        final RtOrganization org = new RtOrganization(
            new ApacheRequest(container.home()),
            this.user(),
            1
        );
        org.patch(
            Json.createObjectBuilder().add("patch", "test").build()
        );
        final MkQuery query = container.take();
        try {
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtOrganization should be able to compare instances of each other.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final RtOrganization less = new RtOrganization(
            new FakeRequest(),
            this.user(),
            39
        );
        final RtOrganization greater = new RtOrganization(
            new FakeRequest(),
            this.user(),
            42
        );
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
        MatcherAssert.assertThat(
            less.compareTo(less), Matchers.equalTo(0)
        );
    }

    /**
     * RtOrganization can return a String representation correctly reflecting
     * its URI.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void canRepresentAsString() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "blah")
        ).start();
        final RtOrganization org = new RtOrganization(
            new ApacheRequest(container.home()),
            this.user(),
            1
        );
        try {
            MatcherAssert.assertThat(
                org.toString(),
                Matchers.endsWith("/user/orgs")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Mock user for testing purposes.
     * @return Mock user
     * @throws IOException if an IO exception occurs.
     */
    private User user() throws IOException {
        final User user = Mockito.mock(User.class);
        Mockito.doReturn("testUser").when(user).login();
        return user;
    }

}
