/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

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
final class MkContentTest {

    @Test
    void canGetOwnRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.contents().create(
                MkContentTest.jsonContent("repo.txt", "for repo", "json repo")
            ).repo(),
            Matchers.is(repo)
        );
    }

    @Test
    void canGetOwnPath() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String path = "dummy.txt";
        MatcherAssert.assertThat(
            "Values are not equal",
            contents.create(
                MkContentTest.jsonContent(path, "for path", "path test")
            ).path(),
            Matchers.is(path)
        );
    }

    @Test
    void fetchesJsonRepresentation() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String path = "fake.txt";
        MatcherAssert.assertThat(
            "Values are not equal",
            contents.create(
                MkContentTest.jsonContent(path, "for json", "json test")
            ).json().getString("name"),
            Matchers.is(path)
        );
    }

    @Test
    void fetchesRawRepresentation() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String raw = "raw test €\0";
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

    private static JsonObject jsonContent(
        final String path,
        final String message,
        final String content
    ) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message).add(
                "content",
                DatatypeConverter.printBase64Binary(
                    content.getBytes(StandardCharsets.UTF_8)
                )
            ).build();
    }
}
