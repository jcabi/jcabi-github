/**
 * Copyright (c) 2013-2014, JCabi.com
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
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtForks}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtForksTest {

    /**
     * Fork's organization name in JSON object.
     */
    public static final String ORGANIZATION = "organization";

    /**
     * RtForks should be able to iterate its forks.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesForks() throws Exception {
        final RtForks forks = new RtForks(
            new FakeRequest()
                .withBody("[]"), this.repo()
        );
        MatcherAssert.assertThat(
            forks.iterate("newest"),
            Matchers.<Fork>iterableWithSize(0)
        );
    }

    /**
     * RtForks should be able to create a new fork.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void createsFork() throws Exception {
        final String organization = RandomStringUtils.randomNumeric(10);
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            fork(organization).toString()
        );
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_ACCEPTED,
                fork(organization).toString()
            )
        ).next(answer).start();
        final Repo owner = Mockito.mock(Repo.class);
        final Coordinates coordinates = new Coordinates.Simple(
            "test_user", "test_repo"
        );
        Mockito.doReturn(coordinates).when(owner).coordinates();
        final RtForks forks = new RtForks(
            new JdkRequest(container.home()),
            owner
        );
        final Fork fork = forks.create(organization);
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.POST)
        );
        MatcherAssert.assertThat(
            fork.json().getString(ORGANIZATION),
            Matchers.equalTo(organization)
        );
        container.stop();
    }

    /**
     * Create and return repo for testing.
     *
     * @return Repo
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "forks"))
            .when(repo).coordinates();
        return repo;
    }

    /**
     * Create and return JsonObject to test.
     * @param organization The organization of the fork
     * @return JsonObject
     */
    private static JsonObject fork(
        final String organization) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add(ORGANIZATION, organization)
            .build();
    }
}
