/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Repo;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkContent}.
 * @since 0.8
 */
public final class MkContentTest {

    @Test
    public void canGetOwnRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Contents contents = repo.contents();
        final Content content = contents.create(
            MkContentTest.jsonContent("repo.txt", "for repo", "json repo")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            content.repo(),
            Matchers.is(repo)
        );
    }

    @Test
    public void canGetOwnPath() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String path = "dummy.txt";
        final Content content = contents.create(
            MkContentTest.jsonContent(path, "for path", "path test")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            content.path(),
            Matchers.is(path)
        );
    }

    @Test
    public void fetchesJsonRepresentation() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String path = "fake.txt";
        final Content content = contents.create(
            MkContentTest.jsonContent(path, "for json", "json test")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            // @checkstyle MultipleStringLiterals (1 line)
            content.json().getString("name"),
            Matchers.is(path)
        );
    }

    @Test
    public void fetchesRawRepresentation() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String raw = "raw test \u20ac\u0000";
        try (
            InputStream stream = contents.create(
                MkContentTest.jsonContent("raw.txt", "for raw", raw)
            ).raw()
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                IOUtils.toString(stream, StandardCharsets.UTF_8),
                Matchers.is(raw)
            );
        }
    }

    /**
     * Get a JSON object for content creation.
     * @param path The path of the file
     * @param message Commit message
     * @param content File content
     * @return JSON representation of content attributes
     */
    private static JsonObject jsonContent(
        final String path,
        final String message,
        final String content
    ) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message)
            .add(
                "content",
                DatatypeConverter.printBase64Binary(
                    content.getBytes(StandardCharsets.UTF_8)
                )
            ).build();
    }
}
