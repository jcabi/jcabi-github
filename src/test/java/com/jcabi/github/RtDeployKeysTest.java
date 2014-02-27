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

import com.jcabi.aspects.Immutable;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
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
     *
     * @todo #119 RtDepoyKeys should iterate multiple deploy keys. Let's
     *  implement a test here and a method of RtDeployKeys. The method should
     *  iterate multiple deploy keys.
     *  See how it's done in other classes with GhPagination.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
    @Test
    @Ignore
    public void canFetchNonEmptyListOfDeployKeys() {
        // to be implemented
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
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "keys"))
            .when(repo).coordinates();
        return repo;
    }
}
