/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Gitignores;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkGitignores}.
 * @since 0.8
 */
public final class MkGitignoresTest {
    @Test
    public void canFetchSingleRawTemplate() throws IOException {
        final Gitignores gitignores = new MkGitHub().gitignores();
        MatcherAssert.assertThat(
            "String does not start with expected value",
            gitignores.template("Java"),
            Matchers.startsWith("*.class")
        );
    }

    @Test
    public void canIterateOverTemplates() throws IOException {
        final Gitignores gitignores = new MkGitHub().gitignores();
        MatcherAssert.assertThat(
            "Collection is not empty",
            gitignores.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
