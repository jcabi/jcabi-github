/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.tools.ToolProvider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@code README.md}.
 *
 * @since 0.24
 */
final class ReadmeTest {

    @Test
    void compilesFirstCodeSample(@TempDir final Path temp) throws IOException {
        final String readme = new String(
            Files.readAllBytes(Paths.get("README.md")),
            StandardCharsets.UTF_8
        );
        final int start = readme.indexOf('\n', readme.indexOf("```java")) + 1;
        final Path source = temp.resolve("Main.java");
        Files.write(
            source,
            readme.substring(start, readme.indexOf("```", start))
                .getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "The first code sample of README.md cannot be compiled",
            ToolProvider.getSystemJavaCompiler().run(
                null, null, null,
                "-proc:none",
                "-d", temp.toString(),
                "-cp", System.getProperty("java.class.path"),
                source.toString()
            ),
            Matchers.equalTo(0)
        );
    }
}
