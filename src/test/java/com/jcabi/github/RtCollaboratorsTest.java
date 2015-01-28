/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.github.mock.MkStorage;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link RtCollaborators}.
 * @author Aleksey Popov (alopen@yandex.ru)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtCollaboratorsTest {
    /**
     * RtCollaborators can iterate over a list of collaborators.
     * @throws Exception if any error occurs.
     */
    @Test
    public void canIterate() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(RtCollaboratorsTest.json("octocat"))
                    .add(RtCollaboratorsTest.json("dummy"))
                    .build().toString()
            )
        ).start();
        final Collaborators users = new RtCollaborators(
            new JdkRequest(container.home()),
            this.repo()
        );
        MatcherAssert.assertThat(
            users.iterate(),
            Matchers.<User>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * User can be added to a repo as a collaborator.
     * @throws Exception if any error occurs.
     */
    @Test
    public void userCanBeAddedAsCollaborator() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_NO_CONTENT,
                Json.createArrayBuilder()
                    .add(RtCollaboratorsTest.json("octocat2"))
                    .add(RtCollaboratorsTest.json("dummy"))
                    .build().toString()
            )
        ).start();
        final Collaborators users = new RtCollaborators(
            new JdkRequest(container.home()),
            this.repo()
        );
        try {
            users.add("dummy1");
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * User can be checked for being a collaborator.
     * @throws Exception if any error occurs.
     */
    @Test
    public void userCanBeTestForBeingCollaborator() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_NO_CONTENT,
                Json.createArrayBuilder()
                    .add(RtCollaboratorsTest.json("octocat2"))
                    .add(RtCollaboratorsTest.json("dummy"))
                    .build().toString()
            )
        ).start();
        final Collaborators users = new RtCollaborators(
            new JdkRequest(container.home()),
            this.repo()
        );
        try {
            MatcherAssert.assertThat(
                users.isCollaborator("octocat2"),
                Matchers.equalTo(true)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * User can be removed from a list of collaborators.
     * @throws Exception if any error occurs.
     */
    @Test
    public void userCanBeRemoved() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_NO_CONTENT,
                Json.createArrayBuilder()
                    .add(RtCollaboratorsTest.json("octocat2"))
                    .add(RtCollaboratorsTest.json("dummy"))
                    .build().toString()
            )
        ).start();
        final Collaborators users = new RtCollaborators(
            new JdkRequest(container.home()),
            this.repo()
        );
        try {
            users.remove("dummy");
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param login Username to login
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonValue json(final String login) throws Exception {
        return Json.createObjectBuilder()
            .add("login", login)
            .build();
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private Repo repo() throws Exception {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "collaboratorrepo"))
            .when(repo).coordinates();
        Mockito.doReturn(new MkGithub(new MkStorage.InFile(), "userLogin"))
            .when(repo).github();
        return repo;
    }
}
