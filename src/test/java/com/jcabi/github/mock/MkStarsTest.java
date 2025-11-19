/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Stars;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkStars.
 * @since 0.1
 */
public final class MkStarsTest {

    @Test
    public void starsRepository() throws IOException {
        final Stars stars = new MkGitHub().randomRepo().stars();
        stars.star();
        MatcherAssert.assertThat(
            "Values are not equal",
            stars.starred(),
            Matchers.is(true)
        );
    }

    @Test
    public void unstarsRepository() throws IOException {
        final Stars stars = new MkGitHub().randomRepo().stars();
        stars.star();
        stars.unstar();
        MatcherAssert.assertThat(
            "Values are not equal",
            stars.starred(),
            Matchers.is(false)
        );
    }
}
