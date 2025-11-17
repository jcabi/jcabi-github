/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Repo}.
 */
public final class RepoTest {

    /**
     * Repo.Smart can fetch description from Repo.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchDescription() throws Exception {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("description", "hello, world!")
                .build()
        ).when(repo).json();
        MatcherAssert.assertThat(
            new Repo.Smart(repo).description(),
            Matchers.containsString("world!")
        );
    }

    /**
     * Repo.Smart can fetch private status from Repo.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchPrivateStatus() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.patch(
            Json.createObjectBuilder()
                .add("private", true)
                .build()
        );
        MatcherAssert.assertThat(
            new Repo.Smart(repo).isPrivate(),
            Matchers.is(true)
        );
    }

}
