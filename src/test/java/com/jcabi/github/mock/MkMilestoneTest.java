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
import org.junit.Test;

/**
 * Unit tests for the MkUser class.
 *
 */
public class MkMilestoneTest {

    /**
     * MkMilestone returns a repo with same coordinates.
     */
    @Test
    public final void returnsSameCoordinatesRepo() throws IOException {
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
            repo.coordinates(),
            Matchers.equalTo(coordinates)
        );
    }

}
