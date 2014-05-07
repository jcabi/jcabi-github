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

import com.jcabi.http.Response;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.response.JsonResponse;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtSearch}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (100 lines)
 */
public final class RtSearchTest {

    /**
     * RtSearch can search for repos.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForRepos() throws Exception {
        final String coords = "test-user1/test-repo1";
        final Search search = new RtGithub(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("total_count", 1)
                    .add(
                        "items", Json.createArrayBuilder().add(
                            Json.createObjectBuilder().add("full_name", coords)
                        )
                    ).build().toString()
            )
        ).search();
        MatcherAssert.assertThat(
            search.repos("test", "stars", "desc").iterator().next()
                .coordinates().toString(),
            Matchers.equalTo(coords)
        );
    }

    /**
     * RtSearch can search for issues.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForIssues() throws Exception {
        final int number = 1;
        final Search search = new RtGithub(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("total_count", 1)
                    .add(
                        "items", Json.createArrayBuilder().add(
                            Json.createObjectBuilder().add(
                                "url", String.format(
                                    // @checkstyle LineLength (1 line)
                                    "https://api.github.com/repos/user/repo/issues/%s",
                                    number
                                )
                            ).add("number", number)
                        )
                    ).build().toString()
            )
        ).search();
        MatcherAssert.assertThat(
            search.issues("test", "created", "desc").iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * RtSearch can search for users.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForUsers() throws Exception {
        final String login = "test-user";
        final Search search = new RtGithub(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("total_count", 1)
                    .add(
                        "items", Json.createArrayBuilder().add(
                            Json.createObjectBuilder().add("login", login)
                        )
                    ).build().toString()
            )
        ).search();
        MatcherAssert.assertThat(
            search.users("test", "joined", "desc").iterator().next().login(),
            Matchers.equalTo(login)
        );
    }

    /**
     * RtSearch can read non-unicode.
     * @throws Exception if any problem inside
     */
    @Test
    public void readNonUnicode() throws Exception {
        final Response resp = new FakeRequest()
            .withBody("{\"help\": \"\u001Fblah\u0001cwhoa\u0000!\"}").fetch();
        final JsonResponse response = new JsonResponse(resp);
        MatcherAssert.assertThat(
            response.json().readObject().getString("help"),
            Matchers.is("\u001Fblah\u0001cwhoa\u0000!")
        );
    }

}
