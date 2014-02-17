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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtFork}.
 *
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
public final class RtForkTest {

    /**
     * RtFork can patch comment and return new json.
     * @throws java.io.IOException if has some problems with json parsing.
     */
    @Test
    public void patchAndCheckJsonFork() throws IOException {
        final String idName = "id";
        final String orgName = "organization";
        final int identifier = 1;
        final String organization = "some organization";
        final String porganization = "some patched organization";
        final MkAnswer first = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(orgName, organization)
                .add(idName, identifier)
                .build().toString()
        );
        final MkAnswer second = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(orgName, porganization)
                .add(idName, identifier)
                .build().toString()
        );
        final MkAnswer third = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(orgName, organization)
                .add(idName, identifier)
                .build().toString()
        );
        final MkContainer container =
            new MkGrizzlyContainer().next(first).next(second).next(third)
                .start();
        final MkContainer forksContainer = new MkGrizzlyContainer().start();
        final RtRepo repo =
            new RtRepo(
                new MkGithub(),
                new ApacheRequest(forksContainer.home()),
                new Coordinates.Simple("test_user", "test_repo")
            );
        final RtFork fork = new RtFork(
            new ApacheRequest(container.home()), repo, identifier
        );
        fork.patch(Json.createObjectBuilder()
            .add(orgName, porganization)
            .add(idName, identifier)
            .build()
        );
        MatcherAssert.assertThat(
            fork.json().getString(orgName),
            Matchers.equalTo(porganization)
        );
        container.stop();
        forksContainer.stop();
    }
}
