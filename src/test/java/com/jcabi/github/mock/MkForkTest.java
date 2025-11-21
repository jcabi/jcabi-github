/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Fork;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkFork}.
 * @since 0.8
 */
public final class MkForkTest {
    @Test
    public void fetchAsJson() throws IOException {
        final Fork fork = new MkGitHub().randomRepo().forks().create("fork");
        MatcherAssert.assertThat(
            "String does not contain expected value",
            fork.json().toString(),
            Matchers.containsString("{")
        );
    }
}
