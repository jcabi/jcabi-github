/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.github.Stars;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkStars.
 */
public class MkStarsTest {

    /**
     * MkStars can star repository.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void starsRepository() throws Exception {
        final Stars stars = new MkGithub().randomRepo().stars();
        stars.star();
        MatcherAssert.assertThat(
            stars.starred(),
            Matchers.is(true)
        );
    }

    /**
     * MkStars can unstar repository.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void unstarsRepository() throws Exception {
        final Stars stars = new MkGithub().randomRepo().stars();
        stars.star();
        stars.unstar();
        MatcherAssert.assertThat(
            stars.starred(),
            Matchers.is(false)
        );
    }
}
