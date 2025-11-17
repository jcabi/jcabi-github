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
 * Test case for {@link MkChecks}.
 *
 * @since 1.6.1
 */
public final class MkChecksTest {

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
        this.pull = new MkGithub()
            .randomRepo()
            .pulls()
            .create("Test PR", "abcdef8", "abcdef9");
    }

    /**
     * MkChecks can return empty checks by default.
     * @throws IOException If some problem with I/O.
     */
    @Test
    public void returnsEmptyChecksByDefault() throws IOException {
        MatcherAssert.assertThat(
            ((MkChecks) this.pull.checks()).all(),
            Matchers.empty()
        );
    }

    /**
     * MkChecks can create a check.
     * @throws IOException If some problem with I/O.
     */
    @Test
    public void createsCheck() throws IOException {
        final MkChecks checks = (MkChecks) this.pull.checks();
        final Check check = checks.create(
            Check.Status.COMPLETED,
            Check.Conclusion.SUCCESS
        );
        MatcherAssert.assertThat(
            checks.all(),
            Matchers.hasSize(1)
        );
        final Check next = checks.all().iterator().next();
        MatcherAssert.assertThat(
            check,
            Matchers.equalTo(next)
        );
        MatcherAssert.assertThat(
            next.successful(),
            Matchers.is(true)
        );
    }
}
