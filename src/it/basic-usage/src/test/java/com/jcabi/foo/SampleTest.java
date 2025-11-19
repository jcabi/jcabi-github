/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.foo;

import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGitHub;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Sample test.
 * @since 1.0
 */
public final class SampleTest {

    /**
     * Fetches labels from GitHub.
     * @throws Exception If fails
     */
    @Test
    public void fetchesLabelsFromGitHub() throws Exception {
        final GitHub github = new RtGitHub();
        final Repo repo = github.repos().get(
            new Coordinates.Simple("jcabi/jcabi-github")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.labels().iterate().iterator().hasNext(),
            Matchers.equalTo(true)
        );
    }

}
