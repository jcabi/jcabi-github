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

import com.jcabi.aspects.Tv;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtUserOrganizations}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 */
public final class RtUserOrganizationsTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtUserOrganizations can iterate organizations for
     * an unauthenticated user.
     *
     * @throws Exception If a problem occurs
     */
    @Test
    public void canIterateOrganizationsForUnauthUser() throws Exception {
        final String username = "octopus";
        final Github github = new MkGithub();
        final User user = github.users().get(username);
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(org(Tv.THREE, "org11"))
                    .add(org(Tv.FOUR, "org12"))
                    .add(org(Tv.FIVE, "org13"))
                    .build().toString()
            )
        ).start(this.resource.port());
        try {
            final UserOrganizations orgs = new RtUserOrganizations(
                github,
                new ApacheRequest(container.home()),
                user
            );
            MatcherAssert.assertThat(
                orgs.iterate(),
                Matchers.<Organization>iterableWithSize(Tv.THREE)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith(String.format("/users/%s/orgs", username))
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Create and return organization to test.
     * @param number Organization ID
     * @param login Organization login name.
     * @return JsonObject
     */
    private static JsonObject org(final int number, final String login) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("login", login)
            .build();
    }
}
