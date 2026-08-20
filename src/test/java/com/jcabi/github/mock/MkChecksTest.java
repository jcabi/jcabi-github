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
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkChecks}.
 * @since 1.6.1
 */
final class MkChecksTest {

    @Test
    void returnsEmptyChecksByDefault() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            ((MkChecks) MkChecksTest.pull().checks()).all(),
            Matchers.empty()
        );
    }

    @Test
    void createsCheck() throws IOException {
        final MkChecks checks = (MkChecks) MkChecksTest.pull().checks();
        checks.create(Check.Status.COMPLETED, Check.Conclusion.SUCCESS);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            checks.all(),
            Matchers.hasSize(1)
        );
    }

    @Test
    void storesCreatedCheck() throws IOException {
        final MkChecks checks = (MkChecks) MkChecksTest.pull().checks();
        MatcherAssert.assertThat(
            "Created check is not stored",
            checks.create(
                Check.Status.COMPLETED,
                Check.Conclusion.SUCCESS
            ),
            Matchers.equalTo(checks.all().iterator().next())
        );
    }

    @Test
    void createsSuccessfulCheck() throws IOException {
        final MkChecks checks = (MkChecks) MkChecksTest.pull().checks();
        checks.create(Check.Status.COMPLETED, Check.Conclusion.SUCCESS);
        MatcherAssert.assertThat(
            "Created check is not successful",
            checks.all().iterator().next().successful(),
            Matchers.is(true)
        );
    }

    private static Pull pull() throws IOException {
        return new MkGitHub()
            .randomRepo()
            .pulls()
            .create("Test PR", "abcdef8", "abcdef9");
    }
}
