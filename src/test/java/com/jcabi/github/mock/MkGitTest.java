/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import java.io.IOException;
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
     */
    @Test
    public void canFetchOwnRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.git().repo(),
            Matchers.equalTo(repo)
        );
    }

    /**
     * MkGit can return references.
     */
    @Test
    public void givesReferences() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().randomRepo().git().references(),
            Matchers.notNullValue()
        );
    }
}
