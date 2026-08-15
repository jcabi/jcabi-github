/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtGist}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtGistTest {

    /**
     * RtGist should be able to do reads.
     */
    @Test
    void readsFileWithContents() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"hello\":{\"raw_url\":\"world\"}}}"
                )
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "success!"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                new RtGist(
                    new MkGitHub(),
                    new ApacheRequest(container.home()),
                    "test"
                ).read("hello"),
                Matchers.equalTo("success!")
            );
            container.stop();
        }
    }

    @Test
    void writesFileContents() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testFileWrite")
            ).start(RandomPort.port())
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
     * @throws IOException If there is a problem.
     */
    @Test
    void fork() throws IOException {
        try (MkContainer container = new MkGrizzlyContainer()) {
            container.next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"hello\":{\"raw_url\":\"world\"}}}"
                )
            );
            final String success = "success";
            container.next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, success)
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
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, success)
            );
            container.start(RandomPort.port());
            final Gist gist = new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "test"
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                gist.read("hello"),
                Matchers.equalTo(gist.fork().read("hello"))
            );
            container.stop();
        }
    }

    @Test
    void canIterateFiles() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"something\":{\"filename\":\"not null\"}}}"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Files of the gist are absent",
                new Gist.Smart(
                    new RtGist(
                        new MkGitHub(),
                        new ApacheRequest(container.home()),
                        "testGetFiles"
                    )
                ).files(),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void fetchesFilesFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"files\":{\"something\":{\"filename\":\"not null\"}}}"
                )
            ).start(RandomPort.port())
        ) {
            new Gist.Smart(
                new RtGist(
                    new MkGitHub(),
                    new ApacheRequest(container.home()),
                    "testGetFiles"
                )
            ).files();
            MatcherAssert.assertThat(
                "Files are fetched from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/gists/testGetFiles")
            );
        }
    }

    @Test
    void canRepresentAsString() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "String does not end with expected value",
                new RtGist(
                    new MkGitHub(),
                    new ApacheRequest(container.home()),
                    "testToString"
                ).toString(),
                Matchers.endsWith("/gists/testToString")
            );
            container.stop();
        }
    }

    @Test
    void unstarsWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "unstar"
            ).unstar();
            MatcherAssert.assertThat(
                "Gist is not unstarred with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void unstarsWithoutBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtGist(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "unstar"
            ).unstar();
            MatcherAssert.assertThat(
                "Gist is unstarred with a body",
                container.take().body(),
                Matchers.is(Matchers.emptyOrNullString())
            );
        }
    }

    @Test
    void executePatchRequest() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK, "{\"msg\":\"hi\"}"
                )
            ).start(RandomPort.port())
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
