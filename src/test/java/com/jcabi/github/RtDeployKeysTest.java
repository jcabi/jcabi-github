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

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtDeployKeys}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
public final class RtDeployKeysTest {

    /**
     * RtDeployKeys can fetch empty list of deploy keys.
     */
    @Test
    public void canFetchEmptyListOfDeployKeys() {
        final DeployKeys deployKeys = new RtDeployKeys(
            new FakeRequest().withBody("[]"),
            RtDeployKeysTest.repo()
        );
        MatcherAssert.assertThat(
            deployKeys.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * RtDeployKeys can fetch non empty list of deploy keys.
     * @throws IOException If any problems occurs.
     */
    @Test
    // @checkstyle MultipleStringLiteralsCheck (10 lines)
    public void canFetchNonEmptyListOfDeployKeys() throws IOException {
        final JsonObject[] expected = new JsonObject[2];
        expected[0] = Json.createObjectBuilder()
            .add("id", 1)
            .build();
        expected[1] = Json.createObjectBuilder()
            .add("id", 2)
            .build();
        final JsonArray json = Json.createArrayBuilder()
            .add(expected[0])
            .add(expected[1])
            .build();
        // @checkstyle MagicNumberCheck (1 line)
        final MkAnswer.Simple[] answers = new MkAnswer.Simple[3];
        answers[0] = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK, json.toString()
        );
        answers[1] = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK, expected[0].toString()
        );
        answers[2] = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK, expected[1].toString()
        );
        final MkContainer container = new MkGrizzlyContainer().next(answers[0])
            .next(answers[1])
            .next(answers[2])
            .start();
        final RtDeployKeys keys = new RtDeployKeys(
            new JdkRequest(container.home()),
            RtDeployKeysTest.repo()
        );
        try {
            final Iterator<DeployKey> iterator =
                keys.iterate().iterator();
            MatcherAssert.assertThat(
                iterator.next().json(),
                Matchers.equalTo(expected[0])
            );
            checkMethodAndUri(
                container.take(),
                Request.GET, "/repos/owner/repo/keys"
            );
            checkMethodAndUri(
                container.take(),
                Request.GET, "/repos/owner/repo/keys/1"
            );
            MatcherAssert.assertThat(
                iterator.next().json(),
                Matchers.equalTo(expected[1])
            );
            checkMethodAndUri(
                container.take(),
                Request.GET, "/repos/owner/repo/keys/2"
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtDeployKeys can fetch single deploy key.
     * @throws IOException If some problem inside
     */
    @Test
    public void canFetchSingleDeployKey() throws IOException {
        final int number = 1;
        final DeployKeys keys = new RtDeployKeys(
            // @checkstyle MultipleStringLiterals (1 line)
            new FakeRequest().withBody(String.format("{\"id\":%d}", number)),
            RtDeployKeysTest.repo()
        );
        MatcherAssert.assertThat(
            keys.get(number).json().getInt("id"),
            Matchers.equalTo(number)
        );
    }

    /**
     * RtDeployKeys can create a key.
     * @throws IOException If some problem inside.
     */
    @Test
    public void canCreateDeployKey() throws IOException {
        final int number = 2;
        final DeployKeys keys = new RtDeployKeys(
            new FakeRequest()
                .withStatus(HttpURLConnection.HTTP_CREATED)
                .withBody(String.format("{\"id\":%d}", number)),
            RtDeployKeysTest.repo()
        );
        MatcherAssert.assertThat(
            keys.create("Title", "Key").number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * RtDeployKeys can delete a deploy key.
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canDeleteDeployKey() throws Exception {
        final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start();
        final RtDeployKeys deployKeys = new RtDeployKeys(
            new JdkRequest(container.home()),
            RtDeployKeysTest.repo()
        );
        deployKeys.remove(1);
        try {
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.isEmptyString()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Check if the given query's method is equal to the provided,
     * and its URI ends with the given String.
     * @param query The query to be checked.
     * @param method Expected method.
     * @param uri Expected URI ending
     */
    private static void checkMethodAndUri(final MkQuery query,
        final String method, final String uri) {
        MatcherAssert.assertThat(
            query.method(),
            Matchers.equalTo(method)
        );
        MatcherAssert.assertThat(
            query.uri().toString(),
            Matchers.endsWith(uri)
        );
    }
    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("owner", "repo"))
            .when(repo).coordinates();
        return repo;
    }
}
