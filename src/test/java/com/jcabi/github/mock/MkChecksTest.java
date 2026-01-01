/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Check;
import com.jcabi.github.Pull;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkChecks}.
 * @since 1.6.1
 */
final class MkChecksTest {

    /**
     * Pull request.
     */
    private transient Pull pull;

    /**
     * Set up.
     * @throws IOException If some problem with I/O.
     */
    @BeforeEach
    void setUp() throws IOException {
        this.pull = new MkGitHub()
            .randomRepo()
            .pulls()
            .create("Test PR", "abcdef8", "abcdef9");
    }

    /**
     * MkChecks can return empty checks by default.
     * @throws IOException If some problem with I/O.
     */
    @Test
    void returnsEmptyChecksByDefault() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            ((MkChecks) this.pull.checks()).all(),
            Matchers.empty()
        );
    }

    /**
     * MkChecks can create a check.
     * @throws IOException If some problem with I/O.
     */
    @Test
    void createsCheck() throws IOException {
        final MkChecks checks = (MkChecks) this.pull.checks();
        final Check check = checks.create(
            Check.Status.COMPLETED,
            Check.Conclusion.SUCCESS
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            checks.all(),
            Matchers.hasSize(1)
        );
        final Check next = checks.all().iterator().next();
        MatcherAssert.assertThat(
            "Values are not equal",
            check,
            Matchers.equalTo(next)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            next.successful(),
            Matchers.is(true)
        );
    }
}
