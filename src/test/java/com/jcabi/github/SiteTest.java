/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for the Maven site sources in {@code src/site}.
 *
 * @since 1.11
 */
final class SiteTest {

    @Test
    void linksJavadocToPublishedDirectory() throws IOException {
        final StringBuilder text = new StringBuilder(0);
        for (final File file : FileUtils.listFiles(new File("src/site"), null, true)) {
            text.append(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
        }
        MatcherAssert.assertThat(
            "Site links to a versioned JavaDoc directory that is never published",
            text.toString(),
            Matchers.not(Matchers.containsString("apidocs-"))
        );
    }
}
