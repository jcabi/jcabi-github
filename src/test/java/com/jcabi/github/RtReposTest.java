/**
 * Copyright (c) 2013-2018, jcabi.com
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
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link com.jcabi.github.Repos}.
 * @author Gena Svarovski (g.svarovski@gmail.com)
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
public final class RtReposTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();
    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    /**
     * RtRepos can create a repo.
     * @throws Exception if some problem inside
     */
    @Test
    public void createRepo() throws Exception {
        final String owner = "test-owner";
        final String name = "test-repo";
        final String response = response(owner, name).toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
                .start(this.resource.port())
        ) {
            final Github github = Mockito.mock(Github.class);
            final Repos reposMock = Mockito.mock(Repos.class);
            Mockito.doReturn(github).when(reposMock).github();
            Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();
            Mockito.doReturn(github).when(reposMock).github();
            final RtRepos repos = new RtRepos(
                github,
                new ApacheRequest(container.home())
            );
            final Repo repo = this.rule.repo(repos);
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                repo.coordinates(),
                new IsEqual<>(new Coordinates.Simple(owner, name))
            );
            container.stop();
        }
    }

    /**
     * RtUsers can iterate users.
     * @throws Exception if there is any Error
     */
    @Test
    public void iterateRepos() throws Exception {
        final String identifier = "1";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(response("octocat", identifier))
                        .add(response("dummy", "2"))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final Github github = Mockito.mock(Github.class);
            final Repos repo = Mockito.mock(Repos.class);
            Mockito.doReturn(github).when(repo).github();
            Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();
            Mockito.doReturn(github).when(repo).github();

            final RtRepos repos = new RtRepos(
                github,
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                repos.iterate(identifier),
                Matchers.<Repo>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtRepos can remove a repo.
     * @throws Exception if some problem inside
     */
    @Test
    public void removeRepo() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(this.resource.port())
        ) {
            final Github github = Mockito.mock(Github.class);
            Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();
            final Repos repos = new RtRepos(github,
                new ApacheRequest(container.home()));
            repos.remove(new Coordinates.Simple("", ""));
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.isEmptyString()
            );
            container.stop();
        }
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
            .add("name", name)
            .add("full_name", String.format("%s/%s", owner, name))
            .add(
                "owner",
                Json.createObjectBuilder().add("login", owner).build()
            )
            .build();
    }

}
