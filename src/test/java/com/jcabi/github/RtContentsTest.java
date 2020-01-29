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

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtContentsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtContents can fetch the default branch readme file.
     *
     * @throws Exception if some problem inside.
     */
    @Test
    public void canFetchReadmeFile() throws Exception {
        final String path = "README.md";
        final JsonObject body = Json.createObjectBuilder()
            .add("path", path)
            .build();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString())
            ).start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                repo()
            );
            MatcherAssert.assertThat(
                contents.readme().path(),
                Matchers.is(path)
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/repos/test/contents/readme")
            );
            MatcherAssert.assertThat(
                query.body().length(),
                Matchers.is(0)
            );
        }
    }

    /**
     * RtContents can fetch the readme file from the specified branch.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canFetchReadmeFileFromSpecifiedBranch() throws Exception {
        final String path = "README.md";
        final JsonObject body = Json.createObjectBuilder()
            .add("path", path)
            .build();
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString())
        ).start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                repo()
            );
            MatcherAssert.assertThat(
                contents.readme("test-branch").path(),
                Matchers.is(path)
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/repos/test/contents/readme")
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.is("{\"ref\":\"test-branch\"}")
            );
        }
    }

    /**
     * RtContents can fetch files from the repository.
     *
     * @throws Exception if some problem inside.
     * @checkstyle MultipleStringLiteralsCheck (50 lines)
     */
    @Test
    public void canFetchFilesFromRepository() throws Exception {
        final String path = "test/file";
        final String name = "file";
        final JsonObject body = Json.createObjectBuilder()
            .add("path", path)
            .add("name", name)
            .build();
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("name", name)
                    .build().toString()
            )
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString()))
            .start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                repo()
            );
            final Content.Smart smart = new Content.Smart(
                contents.get(path, "branch1")
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith(
                    "/repos/test/contents/contents/test/file?ref=branch1"
                )
            );
            MatcherAssert.assertThat(
                smart.path(),
                Matchers.is(path)
            );
            MatcherAssert.assertThat(
                smart.name(),
                Matchers.is(name)
            );
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.GET)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith(
                    "/repos/test/contents/contents/test/file?ref=branch1"
                )
            );
        }
    }

    /**
     * RtContents can create a file in the repository.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canCreateFileInRepository() throws Exception {
        final String path = "test/thefile";
        final String name = "thefile";
        final JsonObject body = Json.createObjectBuilder()
            .add("path", path)
            .add("name", name)
            .build();
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().add("content", body)
                    .build().toString()
            )
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString()))
            .start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                repo()
            );
            final JsonObject content = Json.createObjectBuilder()
                .add("path", path)
                .add("message", "theMessage")
                .add("content", "blah")
                .build();
            final Content.Smart smart = new Content.Smart(
                contents.create(content)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith(path)
            );
            MatcherAssert.assertThat(
                smart.path(),
                Matchers.is(path)
            );
            MatcherAssert.assertThat(
                smart.name(),
                Matchers.is(name)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith("/repos/test/contents/contents/test/thefile")
            );
        }
    }

    /**
     * RtContents can delete files from the repository.
     *
     * @throws Exception if a problem occurs.
     * @checkstyle MultipleStringLiteralsCheck (50 lines)
     */
    @Test
    public void canDeleteFilesFromRepository() throws Exception {
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().add(
                    "commit",
                    Json.createObjectBuilder()
                        .add("sha", "commitSha")
                        .build()
                ).build().toString()
            )
        ).start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                repo()
            );
            final RepoCommit commit = contents.remove(
                Json.createObjectBuilder()
                    .add("path", "to/remove")
                    .add("message", "Delete me")
                    .add("sha", "fileSha")
                    .build()
            );
            MatcherAssert.assertThat(
                commit.sha(),
                Matchers.is("commitSha")
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.body(),
                Matchers.allOf(
                    Matchers.containsString("\"message\":\"Delete me\""),
                    Matchers.containsString("\"sha\":\"fileSha\"")
                )
            );
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/repos/test/contents/contents/to/remove")
            );
        }
    }

    /**
     * RtContents can update files into the repository.
     * @throws Exception If any problems during test execution occurs.
     */
    @Test
    public void canUpdateFilesInRepository() throws Exception {
        final String sha = "2f97253a513bbe26658881c29e27910082fef900";
        final JsonObject resp = Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (1 line)
            .add("sha", sha).build();
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().add("commit", resp)
                    .build().toString()
            )
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, resp.toString()))
            .start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                repo()
            );
            final String path = "test.txt";
            final JsonObject json = Json.createObjectBuilder()
                .add("message", "let's change it.")
                .add("content", "bmV3IHRlc3Q=")
                .add("sha", "90b67dda6d5944ad167e20ec52bfed8fd56986c8")
                .build();
            MatcherAssert.assertThat(
                new RepoCommit.Smart(contents.update(path, json)).sha(),
                Matchers.is(sha)
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                query.uri().getPath(),
                Matchers.endsWith(path)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.equalTo(json.toString())
            );
        }
    }

    /**
     * RtContents can iterate through a directory's contents.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void canIterateDirectoryContents() throws Exception {
        final JsonArray body = Json.createArrayBuilder().add(
            Json.createObjectBuilder()
                .add("path", "README.md")
                .build()
        ).add(
            Json.createObjectBuilder()
                .add("path", ".gitignore")
                .build()
        ).build();
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString())
        ).next(new MkAnswer.Simple("{\"path\":\"README.md\"}"))
            .next(new MkAnswer.Simple("{\"path\":\".gitignore\"}"))
            .start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                repo()
            );
            MatcherAssert.assertThat(
                contents.iterate("dir", "branch2"),
                Matchers.<Content>iterableWithSize(2)
            );
        }
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "contents"))
            .when(repo).coordinates();
        final Github github = Mockito.mock(Github.class);
        Mockito.doReturn(github).when(repo).github();
        Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();
        return repo;
    }
}
