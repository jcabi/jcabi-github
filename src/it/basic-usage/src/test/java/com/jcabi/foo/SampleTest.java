/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.foo;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Sample test.
 */
public final class SampleTest {

    /**
     * Fetches labels from Github.
     * @throws Exception If fails
     */
    @Test
    public void fetchesLabelsFromGithub() throws Exception {
        final Github github = new RtGithub();
        final Repo repo = github.repos().get(
            new Coordinates.Simple("jcabi/jcabi-github")
        );
        MatcherAssert.assertThat(
            repo.labels().iterate().iterator().hasNext(),
            Matchers.equalTo(true)
        );
    }

}
