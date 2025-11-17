/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Check;
import com.jcabi.github.Pull;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link MkCheck}.
 *
 * @since 1.6.1
 */
public final class MkCheckTest {

    /**
     * Pull request.
     */
    private transient Pull pull;

    /**
     * Set up.
     * @throws IOException If some problem with I/O.
     */
    @Before
    public void setUp() throws IOException {
        this.pull = new MkGitHub()
            .randomRepo()
            .pulls()
            .create("Test PR", "abcdea8", "abcdea9");
    }

    /**
     * MkChecks can create successful check.
     * @throws IOException If some problem with I/O.
     */
    @Test
    public void createsSuccessfulCheck() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            ((MkChecks) this.pull.checks())
                .create(Check.Status.COMPLETED, Check.Conclusion.SUCCESS)
                .successful(),
            Matchers.is(true)
        );
    }

    /**
     * MkChecks can create failed check.
     * @throws IOException If some problem with I/O.
     */
    @Test
    public void createsFailedCheck() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            ((MkChecks) this.pull.checks())
                .create(
                    Check.Status.COMPLETED,
                    Check.Conclusion.FAILURE
                ).successful(),
            Matchers.is(false)
        );
    }
}
