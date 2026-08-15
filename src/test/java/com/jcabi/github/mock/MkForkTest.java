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
 * Test case for {@link MkFork}.
 * @since 0.8
 */
final class MkForkTest {

    @Test
    void fetchAsJson() throws IOException {
        MatcherAssert.assertThat(
            "String does not contain expected value",
            new MkGitHub().randomRepo().forks().create("fork").json().toString(),
            Matchers.containsString("{")
        );
    }
}
