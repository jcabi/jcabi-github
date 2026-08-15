/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkGitignores}.
 * @since 0.8
 */
final class MkGitignoresTest {

    @Test
    void canFetchSingleRawTemplate() throws IOException {
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new MkGitHub().gitignores().template("Java"),
            Matchers.startsWith("*.class")
        );
    }

    @Test
    void canIterateOverTemplates() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            new MkGitHub().gitignores().iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
