/**
 * Copyright (c) 2013-2014, JCabi.com
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
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (350 lines)
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtContentsTest {

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
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString())
        ).start();
        final RtContents contents = new RtContents(
            new ApacheRequest(container.home()),
            repo()
        );
        try {
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
        } finally {
            container.stop();
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
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString())
        ).start();
        final RtContents contents = new RtContents(
            new ApacheRequest(container.home()),
            repo()
        );
        try {
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
        } finally {
            container.stop();
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
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("name", name)
                    .build().toString()
            )
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString()))
            .start();
        final RtContents contents = new RtContents(
            new ApacheRequest(container.home()),
            repo()
        );
        try {
            final Content.Smart smart = new Content.Smart(
                contents.get(path, "master")
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/repos/test/contents/contents")
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
                Matchers.endsWith("/repos/test/contents/contents/test/file")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtContents can create a file in the repository.
     * @todo #314:15min RtContents#create() should be referencing a get()
     *  method instead of creating its own RtContent object, however,
     *  that method is not yet present. When that has been implemented, update
     *  create() to delegate to get() to obtain a RtContent instance.
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
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().add("content", body)
                    .build().toString()
            )
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString()))
            .start();
        final RtContents contents = new RtContents(
            new ApacheRequest(container.home()),
            repo()
        );
        final JsonObject content = Json.createObjectBuilder()
            .add("path", path)
            .add("message", "theMessage")
            .add("content", "blah")
            .build();
        try {
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
        } finally {
            container.stop();
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
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().add(
                    "commit",
                    Json.createObjectBuilder()
                        .add("sha", "commitSha")
                        .build()
                ).build().toString()
            )
        ).start();
        final RtContents contents = new RtContents(
            new ApacheRequest(container.home()),
            repo()
        );
        try {
            final Commit commit = contents.remove(
                "to/remove",
                "Delete me",
                "fileSha",
                "master",
                Collections.<String, String>emptyMap(),
                Collections.<String, String>emptyMap()
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
        } finally {
            container.stop();
        }
    }

    /**
     * RtContents can update files into the repository.
     * @throws Exception If any problems during test execution occurs.
     */
    @Test
    public void canUpdateFilesInRepository() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{}")
        ).start();
        try {
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
            contents.update(path, json);
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
        } finally {
            container.stop();
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
        return repo;
    }
}
