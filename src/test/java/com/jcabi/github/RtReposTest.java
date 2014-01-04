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
import com.rexsl.test.request.ApacheRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link com.jcabi.github.Repos}.
 * @author Gena Svarovski (g.svarovski@gmail.com)
 * @version $Id$
 */
public final class RtReposTest {
    /**
     * The key for name in JSON.
     */
    private static final String NAME_KEY = "name";

    /**
     * RtRepos can create a repo.
     * @throws Exception if some problem inside
     */
    @Test
    public void createRepo() throws Exception {
        final String owner = "test-owner";
        final String name = "test-repo";
        final String response = response(owner, name).toString();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
            .start();
        final RtRepos repos = new RtRepos(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        final Repo repo = repos.create(request(name));
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.POST)
        );
        MatcherAssert.assertThat(
            repo.coordinates(),
            Matchers.equalTo((Coordinates)new Coordinates.Simple(owner, name))
        );
        container.stop();
    }

    /**
     * Create and return JsonObject to test request.
     *
     * @param name Repo name
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject request(
        final String name) throws Exception {
        return Json.createObjectBuilder()
            .add(NAME_KEY, name)
            .build();
    }

    /**
     * Create and return JsonObject to test response.
     *
     * @param owner Owner name
     * @param name Repo name
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject response(
        final String owner, final String name)
        throws Exception {
        return Json.createObjectBuilder()
            .add(NAME_KEY, name)
            .add("full_name", String.format("%s/%s", owner, name))
            .add(
                "owner",
                Json.createObjectBuilder().add("login", owner).build()
            )
            .build();
    }

}
