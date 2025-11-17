/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Gitignores;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link com.jcabi.github.mock.MkGitignores}.
 * @since 0.8
 */
public final class MkGitignoresTest {
    /**
     * MkGitignores can fetch single gitignore template.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchSingleRawTemplate() throws Exception {
        final Gitignores gitignores = new MkGithub().gitignores();
        MatcherAssert.assertThat(
            gitignores.template("Java"),
            Matchers.startsWith("*.class")
        );
    }

    /**
     * MkGitignores can iterate over templates.
     * @throws Exception if something goes wrong.
     */
    @Test
    public void canIterateOverTemplates() throws Exception {
        final Gitignores gitignores = new MkGithub().gitignores();
        MatcherAssert.assertThat(
            gitignores.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
