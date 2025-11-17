/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Limit;
import com.jcabi.github.Limits;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkLimits}.
 */
public final class MkLimitsTest {

    /**
     * MkLimits can work.
     */
    @Test
    public void worksWithMockedData() throws IOException {
        final Limits limits = new MkGitHub().limits();
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            new Limit.Smart(limits.get(Limits.CORE)).limit(),
            Matchers.greaterThan(0)
        );
    }

}
