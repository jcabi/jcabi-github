/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MkUser class.
 * @since 0.7
 */
final class MkMilestoneTest {

    @Test
    void returnsSameCoordinatesRepo() throws IOException {
        final Coordinates coordinates = new Coordinates.Simple(
            "user",
            "repo"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new MkMilestone(
                new MkStorage.InFile(),
                "login",
                coordinates,
                1
            ).repo().coordinates(),
            Matchers.equalTo(coordinates)
        );
    }
}
