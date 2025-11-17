/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtContents}.
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
     */
    @Test
    public void canFetchReadmeFile() throws IOException {
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
                RtContentsTest.repo()
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
     */
    @Test
    public void canFetchReadmeFileFromSpecifiedBranch() throws IOException {
        final String path = "README.md";
        final JsonObject body = Json.createObjectBuilder()
            .add("path", path)
            .build();
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString())
        ).start(this.resource.port())) {
            final RtContents contents = new RtContents(
                new ApacheRequest(container.home()),
                RtContentsTest.repo()
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
     * @checkstyle MultipleStringLiteralsCheck (50 lines)
     */
    @Test
    public void canFetchFilesFromRepository() throws IOException {
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
                RtContentsTest.repo()
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
     */
    @Test
    public void canCreateFileInRepository() throws IOException {
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
                RtContentsTest.repo()
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
     * @checkstyle MultipleStringLiteralsCheck (50 lines)
     */
    @Test
    public void canDeleteFilesFromRepository() throws IOException {
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
                RtContentsTest.repo()
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
     */
    @Test
    public void canUpdateFilesInRepository() throws IOException {
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
                RtContentsTest.repo()
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
     */
    @Test
    public void canIterateDirectoryContents() throws IOException {
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
                RtContentsTest.repo()
            );
            MatcherAssert.assertThat(
                contents.iterate("dir", "branch2"),
                Matchers.iterableWithSize(2)
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
        return repo;
    }
}
