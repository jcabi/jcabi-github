/**
 * Copyright (c) 2013-2014, jcabi.com
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
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import javax.ws.rs.core.UriBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtStars}.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 */
public final class RtStarsTest {

    /**
     * RtStars can check if repo is starred.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void checkIfRepoStarred() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
            .start();
        final Stars truestars = new RtStars(
            new ApacheRequest(container.home()),
            RtStarsTest.repo("someuser", "starredrepo")
        );
        MatcherAssert.assertThat(
            truestars.starred(), Matchers.is(true)
        );
        final Stars falsestars = new RtStars(
            new ApacheRequest(container.home()),
            RtStarsTest.repo("otheruser", "notstarredrepo")
        );
        MatcherAssert.assertThat(
            falsestars.starred(), Matchers.is(false)
        );
    }

    /**
     * RtStars can star repository.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void starRepository() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
        ).start();
        final String user = "staruser";
        final String repo = "starrepo";
        final Stars stars = new RtStars(
            new ApacheRequest(container.home()),
            RtStarsTest.repo(user, repo)
        );
        try {
            stars.star();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                query.uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath(user)
                        .path(repo)
                        .build()
                        .getPath()
                )
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtStars can unstar repository.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void unstarRepository() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
        ).start();
        final String user = "unstaruser";
        final String repo = "unstarrepo";
        final Stars stars = new RtStars(
            new ApacheRequest(container.home()),
            RtStarsTest.repo(user, repo)
        );
        try {
            stars.unstar();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath(user)
                        .path(repo)
                        .build()
                        .getPath()
                )
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Create and return repo for testing.
     * @param user User
     * @param reponame Repository
     * @return Repo
     */
    private static Repo repo(final String user, final String reponame) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple(user, reponame))
            .when(repo).coordinates();
        return repo;
    }
}
