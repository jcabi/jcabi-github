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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtGists}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtGistsTest {

    /**
     * RtGists can create new files.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canCreateFiles() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"id\":\"1\"}"
            )
        ).start();
        final Gists gists = new RtGists(
            new MkGithub(),
            new ApacheRequest(container.home())
        );
        try {
            MatcherAssert.assertThat(
                gists.create(Collections.singletonMap("test", ""), false),
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                container.take().body(),
                Matchers.startsWith("{\"files\":{\"test\":{\"content\":")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtGists can retrieve a specific Gist.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canRetrieveSpecificGist() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testing")
        ).start();
        final Gists gists = new RtGists(
            new MkGithub(),
            new ApacheRequest(container.home())
        );
        try {
            MatcherAssert.assertThat(
                gists.get("gist"),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtGists can iterate through its contents.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canIterateThrouRtGists() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "[{\"id\":\"hello\"}]"
            )
        ).start();
        final Gists gists = new RtGists(
            new MkGithub(),
            new ApacheRequest(container.home())
        );
        try {
            MatcherAssert.assertThat(
                gists.iterate().iterator().next(),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }
    /**
     * RtGists can remove a gist by name.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void removesGistByName() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_NO_CONTENT,
                ""
            )
        )
            .start();
        final Gists gists = new RtGists(
            new MkGithub(),
            new ApacheRequest(container.home())
        );
        try {
            gists.remove("12234");
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
        } finally {
            container.stop();
        }
    }
}
