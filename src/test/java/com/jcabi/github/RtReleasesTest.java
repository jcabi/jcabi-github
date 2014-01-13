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

import com.rexsl.test.request.FakeRequest;
import java.io.IOException;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleases}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReleasesTest {

    /**
     * RtReleases can fetch empty list of releases.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfReleases() throws Exception {
        final Releases releases = new RtReleases(
            new FakeRequest().withBody("[]"),
            RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * RtReleases can fetch non empty list of releases.
     */
    @Test
    public void canFetchNonEmptyListOfReleases() {
        final int number = 1;
        final Releases releases = new RtReleases(
            new FakeRequest().withBody(
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("id", number)
                        .add("tag_name", "v1.0.0")
                        .add("name", "v1.0.0")
                        .add("body", "Release")
                ).build().toString()
            ),
            RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(
            releases.iterate().iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * RtReleases can fetch a single release.
     * @throws IOException If some problem inside
     */
    @Test
    public void canFetchSingleRelease() throws IOException {
        final Releases releases = new RtReleases(
            new FakeRequest(), RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(releases.get(1), Matchers.notNullValue());
    }

    /**
     * RtReleases can create a release.
     *
     * @todo #123 RtReleases should be able to create a Release. Let's implement
     *  a test here and a method create() of RtReleases.
     *  The method should create a release.
     *  See how it's done in other classes, using Rexsl request/response.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
    @Test
    @Ignore
    public void canCreateRelease() {
        // to be implemented
    }

    /**
     * RtReleases can delete a release.
     *
     * @todo #123 RtReleases should be able to delete a Release. Let's implement
     *  a test here and a method remove() of RtReleases.
     *  The method should remove a release by it's id.
     *  See how it's done in other classes, using Rexsl request/response.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
    @Test
    @Ignore
    public void canDeleteRelease() {
        // to be implemented
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "releases"))
            .when(repo).coordinates();
        return repo;
    }
}
