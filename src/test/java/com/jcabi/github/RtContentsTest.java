/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtContents}.
 * @since 0.8
 */
@Immutable
@ExtendWith(RandomPort.class)
final class RtContentsTest {

    /**
     * Path of the readme file.
     */
    private static final String README = "README.md";

    /**
     * Path of the fetched file.
     */
    private static final String PATH = "test/file";

    /**
     * Name of the fetched file.
     */
    private static final String NAME = "file";

    /**
     * Branch of the fetched file.
     */
    private static final String BRANCH = "branch1";

    /**
     * URI of the fetched file.
     */
    private static final String URI =
        "/repos/test/contents/contents/test/file?ref=branch1";

    @Test
    void fetchesReadmePath() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.README, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Readme has a wrong path",
                RtContentsTest.contents(container).readme().path(),
                Matchers.is(RtContentsTest.README)
            );
        }
    }

    @Test
    void fetchesReadmeFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.README, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            RtContentsTest.contents(container).readme();
            MatcherAssert.assertThat(
                "Readme is fetched from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/test/contents/readme")
            );
        }
    }

    @Test
    void fetchesReadmeWithoutBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.README, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            RtContentsTest.contents(container).readme();
            MatcherAssert.assertThat(
                "Readme is fetched with a body",
                container.take().body().length(),
                Matchers.is(0)
            );
        }
    }

    @Test
    void fetchesReadmePathFromBranch() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.README, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Readme of the branch has a wrong path",
                RtContentsTest.contents(container).readme("test-branch").path(),
                Matchers.is(RtContentsTest.README)
            );
        }
    }

    @Test
    void fetchesReadmeOfBranchFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.README, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            RtContentsTest.contents(container).readme("test-branch");
            MatcherAssert.assertThat(
                "Readme of the branch is fetched from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/test/contents/readme")
            );
        }
    }

    @Test
    void sendsBranchWhileFetchingReadme() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.README, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            RtContentsTest.contents(container).readme("test-branch");
            MatcherAssert.assertThat(
                "Branch is not sent while fetching the readme",
                container.take().body(),
                Matchers.is("{\"ref\":\"test-branch\"}")
            );
        }
    }

    @Test
    void fetchesFileFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.PATH, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            RtContentsTest.contents(container)
                .get(RtContentsTest.PATH, RtContentsTest.BRANCH);
            MatcherAssert.assertThat(
                "File is fetched from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(RtContentsTest.URI)
            );
        }
    }

    @Test
    void fetchesFileWithGetMethod() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.PATH, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            RtContentsTest.contents(container)
                .get(RtContentsTest.PATH, RtContentsTest.BRANCH);
            MatcherAssert.assertThat(
                "File is not fetched with GET",
                container.take().method(),
                Matchers.equalTo(Request.GET)
            );
        }
    }

    @Test
    void fetchesPathOfFile() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.PATH, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Fetched file has a wrong path",
                new Content.Smart(
                    RtContentsTest.contents(container)
                        .get(RtContentsTest.PATH, RtContentsTest.BRANCH)
                ).path(),
                Matchers.is(RtContentsTest.PATH)
            );
        }
    }

    @Test
    void fetchesNameOfFile() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.PATH, RtContentsTest.NAME
                )
            ).next(
                RtContentsTest.answer(
                    RtContentsTest.PATH, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Fetched file has a wrong name",
                new Content.Smart(
                    RtContentsTest.contents(container)
                        .get(RtContentsTest.PATH, RtContentsTest.BRANCH)
                ).name(),
                Matchers.is(RtContentsTest.NAME)
            );
        }
    }

    @Test
    void fetchesJsonOfFileFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtContentsTest.answer(
                    RtContentsTest.PATH, RtContentsTest.NAME
                )
            ).next(
                RtContentsTest.answer(
                    RtContentsTest.PATH, RtContentsTest.NAME
                )
            ).start(RandomPort.port())
        ) {
            new Content.Smart(
                RtContentsTest.contents(container)
                    .get(RtContentsTest.PATH, RtContentsTest.BRANCH)
            ).name();
            container.take();
            MatcherAssert.assertThat(
                "JSON of the file is fetched from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(RtContentsTest.URI)
            );
        }
    }

    @Test
    void createsFileAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.created())
                .start(RandomPort.port())
        ) {
            RtContentsTest.create(container);
            MatcherAssert.assertThat(
                "File is created at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("test/thefile")
            );
        }
    }

    @Test
    void createsFileWithPath() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.created())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created file has a wrong path",
                new Content.Smart(RtContentsTest.create(container)).path(),
                Matchers.is("test/thefile")
            );
        }
    }

    @Test
    void createsFileWithName() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.created())
                .next(RtContentsTest.answer("test/thefile", "thefile"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created file has a wrong name",
                new Content.Smart(RtContentsTest.create(container)).name(),
                Matchers.is("thefile")
            );
        }
    }

    @Test
    void fetchesJsonOfCreatedFileFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.created())
                .next(RtContentsTest.answer("test/thefile", "thefile"))
                .start(RandomPort.port())
        ) {
            new Content.Smart(RtContentsTest.create(container)).name();
            container.take();
            MatcherAssert.assertThat(
                "JSON of the created file is fetched from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/test/contents/contents/test/thefile")
            );
        }
    }

    @Test
    void removesFileFromRepository() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.commit("commitSha"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Removal commit has a wrong SHA",
                RtContentsTest.remove(container).sha(),
                Matchers.is("commitSha")
            );
        }
    }

    @Test
    void sendsRemovalRequestBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.commit("commitSha"))
                .start(RandomPort.port())
        ) {
            RtContentsTest.remove(container);
            MatcherAssert.assertThat(
                "Removal request has a wrong body",
                container.take().body(),
                Matchers.allOf(
                    Matchers.containsString("\"message\":\"Delete me\""),
                    Matchers.containsString("\"sha\":\"fileSha\"")
                )
            );
        }
    }

    @Test
    void removesFileAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.commit("commitSha"))
                .start(RandomPort.port())
        ) {
            RtContentsTest.remove(container);
            MatcherAssert.assertThat(
                "File is removed at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/test/contents/contents/to/remove")
            );
        }
    }

    @Test
    void updatesFileInRepository() throws IOException {
        final String sha = "2f97253a513bbe26658881c29e27910082fef900";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.commit(sha))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Update commit has a wrong SHA",
                new RepoCommit.Smart(RtContentsTest.update(container)).sha(),
                Matchers.is(sha)
            );
        }
    }

    @Test
    void updatesFileWithPutMethod() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.commit("aaa"))
                .start(RandomPort.port())
        ) {
            RtContentsTest.update(container);
            MatcherAssert.assertThat(
                "File is not updated with PUT",
                container.take().method(),
                Matchers.equalTo(Request.PUT)
            );
        }
    }

    @Test
    void updatesFileAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.commit("bbb"))
                .start(RandomPort.port())
        ) {
            RtContentsTest.update(container);
            MatcherAssert.assertThat(
                "File is updated at a wrong URI",
                container.take().uri().getPath(),
                Matchers.endsWith("test.txt")
            );
        }
    }

    @Test
    void sendsUpdateRequestBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtContentsTest.commit("ccc"))
                .start(RandomPort.port())
        ) {
            RtContentsTest.update(container);
            MatcherAssert.assertThat(
                "Update request has a wrong body",
                container.take().body(),
                Matchers.equalTo(RtContentsTest.change().toString())
            );
        }
    }

    @Test
    void iteratesDirectoryContents() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder().add(
                        Json.createObjectBuilder()
                            .add("path", RtContentsTest.README)
                    ).add(
                        Json.createObjectBuilder()
                            .add("path", ".gitignore")
                    ).build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Directory has a wrong amount of contents",
                RtContentsTest.contents(container).iterate("dir", "branch2"),
                Matchers.iterableWithSize(2)
            );
        }
    }

    /**
     * Contents served by the given container.
     * @param container Container to serve the contents
     * @return Contents
     * @throws IOException If there is any I/O problem
     */
    private static RtContents contents(final MkContainer container)
        throws IOException {
        return new RtContents(
            new ApacheRequest(container.home()),
            RtContentsTest.repo()
        );
    }

    /**
     * Create a file through the given container.
     * @param container Container to serve the contents
     * @return Created content
     * @throws IOException If there is any I/O problem
     */
    private static Content create(final MkContainer container)
        throws IOException {
        return RtContentsTest.contents(container).create(
            Json.createObjectBuilder()
                .add("path", "test/thefile")
                .add("message", "theMessage")
                .add("content", "blah")
                .build()
        );
    }

    /**
     * Remove a file through the given container.
     * @param container Container to serve the contents
     * @return Removal commit
     * @throws IOException If there is any I/O problem
     */
    private static RepoCommit remove(final MkContainer container)
        throws IOException {
        return RtContentsTest.contents(container).remove(
            Json.createObjectBuilder()
                .add("path", "to/remove")
                .add("message", "Delete me")
                .add("sha", "fileSha")
                .build()
        );
    }

    /**
     * Update a file through the given container.
     * @param container Container to serve the contents
     * @return Update commit
     * @throws IOException If there is any I/O problem
     */
    private static RepoCommit update(final MkContainer container)
        throws IOException {
        return RtContentsTest.contents(container)
            .update("test.txt", RtContentsTest.change());
    }

    /**
     * The change to apply to a file.
     * @return JSON of the change
     */
    private static JsonObject change() {
        return Json.createObjectBuilder()
            .add("message", "let's change it.")
            .add("content", "bmV3IHRlc3Q=")
            .add("sha", "90b67dda6d5944ad167e20ec52bfed8fd56986c8")
            .build();
    }

    /**
     * Answer with a single content.
     * @param path Path of the content
     * @param name Name of the content
     * @return Answer
     */
    private static MkAnswer answer(final String path, final String name) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add("path", path)
                .add("name", name)
                .build().toString()
        );
    }

    /**
     * Answer with a created content.
     * @return Answer
     */
    private static MkAnswer created() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            Json.createObjectBuilder().add(
                "content",
                Json.createObjectBuilder()
                    .add("path", "test/thefile")
                    .add("name", "thefile")
            ).build().toString()
        );
    }

    /**
     * Answer with a commit.
     * @param sha SHA of the commit
     * @return Answer
     */
    private static MkAnswer commit(final String sha) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder().add(
                "commit",
                Json.createObjectBuilder().add("sha", sha)
            ).build().toString()
        );
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
