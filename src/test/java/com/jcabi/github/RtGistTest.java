/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtGist}.
 *
 */
public final class RtGistTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtGist should be able to do reads.
     *
     * @checkstyle MultipleStringLiteralsCheck (20 lines)
     */
    @Test
    public void readsFileWithContents() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"hello\":{\"raw_url\":\"world\"}}}"
                )
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "success!"))
                .start(this.resource.port())) {
            final RtGist gist = new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "test"
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                gist.read("hello"),
                Matchers.equalTo("success!")
            );
            container.stop();
        }
    }

    /**
     * RtGist should be able to do writes.
     */
    @Test
    public void writesFileContents() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testFileWrite")
            ).start(this.resource.port())
        ) {
            final RtGist gist = new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "testWrite"
            );
            gist.write("testFile", "testContent");
            MatcherAssert.assertThat(
                "String does not contain expected value",
                container.take().body(),
                Matchers.containsString(
                    "\"testFile\":{\"content\":\"testContent\"}"
                )
            );
            container.stop();
        }
    }

    /**
     * RtGist can fork itself.
     *
     * @throws IOException If there is a problem.
     */
    @Test
    public void fork() throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()) {
            container.next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"hello\":{\"raw_url\":\"world\"}}}"
                )
            );
            final String fileContent = "success";
            container.next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, fileContent)
            );
            container.next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"id\": \"forked\"}"
                )
            );
            container.next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"hello\":{\"raw_url\":\"world\"}}}"
                )
            );
            container.next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, fileContent)
            );
            container.start(this.resource.port());
            final Gist gist = new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "test"
            );
            final String content = gist.read("hello");
            final Gist forkedGist = gist.fork();
            MatcherAssert.assertThat(
                "Values are not equal",
                forkedGist.read("hello"),
                Matchers.equalTo(content)
            );
            container.stop();
        }
    }

    /**
     * Gist.Smart can iterate through its files.
     *
     */
    @Test
    public void canIterateFiles() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"something\":{\"filename\":\"not null\"}}}"
                )
            ).start(this.resource.port())
        ) {
            final Gist.Smart smart = new Gist.Smart(
                new RtGist(
                    new MkGitHub(),
                    new ApacheRequest(container.home()),
                    "testGetFiles"
                )
            );
            MatcherAssert.assertThat(
                "Value is null",
                smart.files(),
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                container.take().uri().toString(),
                Matchers.endsWith("/gists/testGetFiles")
            );
            container.stop();
        }
    }

    /**
     * RtGist can return a String representation correctly reflecting its URI.
     *
     */
    @Test
    public void canRepresentAsString() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer()
            .start(this.resource.port())
        ) {
            final RtGist gist = new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "testToString"
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                gist.toString(),
                Matchers.endsWith("/gists/testToString")
            );
            container.stop();
        }
    }

    /**
     * RtGist can unstar a starred Gist.
     */
    @Test
    public void canUnstarAGist() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(this.resource.port())
        ) {
            final RtGist gist = new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "unstar"
            );
            gist.unstar();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.body(),
                Matchers.is(Matchers.emptyOrNullString())
            );
            container.stop();
        }
    }

    /**
     * RtGist can execute PATCH request.
     *
     */
    @Test
    public void executePatchRequest() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK, "{\"msg\":\"hi\"}"
                )
            ).start(this.resource.port())
        ) {
            final RtGist gist = new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "patch"
            );
            gist.patch(
                Json.createObjectBuilder()
                    .add("content", "hi you!")
                    .build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
            container.stop();
        }
    }
}
