/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MkUser class.
 * @since 0.7
 */
public final class MkMilestoneTest {

    @Test
    public void returnsSameCoordinatesRepo() throws IOException {
        final Coordinates coordinates = new Coordinates.Simple(
            "user",
            "repo"
        );
        final MkMilestone milestone = new MkMilestone(
            new MkStorage.InFile(),
            "login",
            coordinates,
            1
        );
        final Repo repo = milestone.repo();
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.coordinates(),
            Matchers.equalTo(coordinates)
        );
    }

}
