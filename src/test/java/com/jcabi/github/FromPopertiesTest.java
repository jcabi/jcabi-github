/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FromProperties}.
 * @since 0.37
 */
public final class FromPopertiesTest {

    @Test
    public void formatsUserAgent() {
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new FromProperties("jcabigithub.properties").format(),
            Matchers.startsWith("jcabi-github ")
        );
    }

    @Test(expected = NullPointerException.class)
    public void throwsExceptionOnMissingFile() {
        new FromProperties("missing.properties").format();
    }

}
