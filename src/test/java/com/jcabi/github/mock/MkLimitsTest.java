/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Limit;
import com.jcabi.github.Limits;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkLimits}.
 */
public final class MkLimitsTest {

    /**
     * MkLimits can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void worksWithMockedData() throws Exception {
        final Limits limits = new MkGithub().limits();
        MatcherAssert.assertThat(
            new Limit.Smart(limits.get(Limits.CORE)).limit(),
            Matchers.greaterThan(0)
        );
    }

}
