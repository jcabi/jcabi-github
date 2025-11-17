/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Repo}.
 * @since 0.1
 */
public final class RepoTest {

    /**
     * Repo.Smart can fetch description from Repo.
     */
    @Test
    public void canFetchDescription() throws IOException {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("description", "hello, world!")
                .build()
        ).when(repo).json();
        MatcherAssert.assertThat(
            "String does not contain expected value",
            new Repo.Smart(repo).description(),
            Matchers.containsString("world!")
        );
    }

    /**
     * Repo.Smart can fetch private status from Repo.
     */
    @Test
    public void canFetchPrivateStatus() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        repo.patch(
            Json.createObjectBuilder()
                .add("private", true)
                .build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new Repo.Smart(repo).isPrivate(),
            Matchers.is(true)
        );
    }

}
