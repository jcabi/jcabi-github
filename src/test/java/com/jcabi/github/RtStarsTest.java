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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtStars}.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @todo #919:30min Implement star() and unstar() operations.
 * Don't forget about unit tests.
 * See https://developer.github.com/v3/activity/starring/ for details.
 */
public final class RtStarsTest {

    /**
     * User for star/unstar test.
     */
    public static final String DUMMY_USER = "dummyuser";
    /**
     * Repository for star/unstar test.
     */
    public static final String DUMMY_REPO = "dummyrepo";

    /**
     * Check if repo is starred.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void checkIfRepoStarred() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
            .start();
        final Stars stars = new RtStars(
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            stars.starred("someuser", "starredrepo"), Matchers.is(true)
        );
        MatcherAssert.assertThat(
            stars.starred("otheruser", "notstarredrepo"), Matchers.is(false)
        );
    }

    /**
     * Check for starring repository.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void checkStar() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
        ).start();
        final Stars stars = new RtStars(
            new ApacheRequest(container.home())
        );
        try {
            stars.star(DUMMY_USER, DUMMY_REPO);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                query.uri().getPath(),
                Matchers.allOf(
                    Matchers.containsString(DUMMY_USER),
                    Matchers.containsString(DUMMY_REPO)
                )
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Check for unstarring repository.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void checkUnstar() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
        ).start();
        final Stars stars = new RtStars(
            new ApacheRequest(container.home())
        );
        try {
            stars.unstar(DUMMY_USER, DUMMY_REPO);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.uri().getPath(),
                Matchers.allOf(
                    Matchers.containsString(DUMMY_USER),
                    Matchers.containsString(DUMMY_REPO)
                )
            );
        } finally {
            container.stop();
        }
    }
}
