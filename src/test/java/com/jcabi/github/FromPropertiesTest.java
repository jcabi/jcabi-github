/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FromProperties}.
 * @since 0.37
 */
final class FromPropertiesTest {

    @Test
    void formatsUserAgent() {
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new FromProperties("jcabigithub.properties").format(),
            Matchers.startsWith("jcabi-github ")
        );
    }

    @Test
    void throwsExceptionOnMissingFile() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new FromProperties("missing.properties").format(),
            "Should throw when properties file is missing"
        );
    }

}
