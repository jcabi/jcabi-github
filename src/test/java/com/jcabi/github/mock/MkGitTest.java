/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkGit}.
 *
 * @since 0.8
 */
public final class MkGitTest {

    /**
     * MkGit can fetch its own repo.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void canFetchOwnRepo() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        MatcherAssert.assertThat(
            repo.git().repo(),
            Matchers.equalTo(repo)
        );
    }

    /**
     * MkGit can return references.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void givesReferences() throws Exception {
        MatcherAssert.assertThat(
            new MkGithub().randomRepo().git().references(),
            Matchers.notNullValue()
        );
    }
}
