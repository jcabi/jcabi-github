/**
 * Copyright (c) 2013-2018, jcabi.com
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
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
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
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

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
     *
     * @throws IOException If some problem inside.
     */
    @Test
    public void canFetchNonEmptyListOfDeployKeys() throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(key(1))
                    .add(key(2))
                    .build().toString()
            )
        )) {
            container.start(this.resource.port());
            MatcherAssert.assertThat(
                new RtDeployKeys(
                    new ApacheRequest(container.home()),
                    RtDeployKeysTest.repo()
                ).iterate(),
                Matchers.<DeployKey>iterableWithSize(2)
            );
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
            // @checkstyle MultipleStringLiterals (1 line)
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
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                String.format("{\"id\":%d}", number)
            )
        )) {
            container.start(this.resource.port());
            final DeployKeys keys = new RtDeployKeys(
                new ApacheRequest(container.home()), RtDeployKeysTest.repo()
            );
            MatcherAssert.assertThat(
                keys.create("Title", "Key").number(),
                Matchers.equalTo(number)
            );
        }
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "keys"))
            .when(repo).coordinates();
        final Github github = Mockito.mock(Github.class);
        Mockito.doReturn(github).when(repo).github();
        Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();
        return repo;
    }

    /**
     * Create and return key to test.
     * @param number Deploy Key Id
     * @return JsonObject
     */
    private static JsonObject key(final int number) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("key", "ssh-rsa AAA")
            .build();
    }
}
