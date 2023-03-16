/**
 * Copyright (c) 2013-2022, jcabi.com
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

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Random;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtChecks}.
 *
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @version $Id$
 * @since 1.5.0
 */
public final class RtChecksTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * Checks whether RtChecks can get all checks.
     *
     * @throws IOException If some problem happens.
     */
    @Test
    public void getsAllChecks() throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.json()
                )
            )
            .start(this.resource.port())) {
            final Checks checks = new RtChecks(
                new JdkRequest(container.home()),
                this.repo().pulls().get(0)
            );
            MatcherAssert.assertThat(
                checks.all(),
                Matchers.iterableWithSize(1)
            );
        }
    }

    /**
     * Creates json response body.
     *
     * @return Json response body.
     */
    private static String json() {
        return Json.createObjectBuilder()
            .add("total_count", Json.createValue(1))
            .add(
                "check_runs",
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add("id", Json.createValue(new Random().nextInt()))
                            .add("status", "completed")
                            .add("conclusion", "success")
                            .build()
                    )
            )
            .build()
            .toString();
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     * @throws IOException If some problem happens.
     */
    private Repo repo() throws IOException {
        final Repo repo = Mockito.mock(Repo.class);
        final Pulls pulls = Mockito.mock(Pulls.class);
        final Pull pull = Mockito.mock(Pull.class);
        final PullRef ref = Mockito.mock(PullRef.class);
        Mockito.doReturn(
                new Coordinates.Simple("volodya-lombrozo", "jtcop")
            ).when(repo)
            .coordinates();
        Mockito.doReturn(pulls).when(repo).pulls();
        Mockito.doReturn(pull).when(pulls).get(0);
        Mockito.doReturn(repo).when(pull).repo();
        Mockito.doReturn(ref).when(pull).head();
        Mockito.doReturn("abcdef1").when(ref).ref();
        return repo;
    }
}
