/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Fork;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkFork}.
 *
 * @since 0.8
 */
public final class MkForkTest {
    /**
     * MkFork can fetch as json object.
     * @throws Exception if any problem inside
     */
    @Test
    public void fetchAsJson() throws Exception {
        final Fork fork = new MkGithub().randomRepo().forks().create("fork");
        MatcherAssert.assertThat(
            fork.json().toString(),
            Matchers.containsString("{")
        );
    }
}
