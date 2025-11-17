/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.User;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkAssignees}.
 * @since 0.7
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class MkAssigneesTest {

    /**
     * MkAssignees can iterate over assignees.
     * @throws Exception Exception If some problem inside
     */
    @Test
    public void iteratesAssignees() throws Exception {
        MatcherAssert.assertThat(
            MkAssigneesTest.repo().assignees().iterate(),
            Matchers.not(Matchers.emptyIterableOf(User.class))
        );
    }

    /**
     * MkAssignees can check if a collaborator is an assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    public void checkCollaboratorIsAssigneeForRepo() throws Exception {
        final Repo repo = MkAssigneesTest.repo();
        repo.collaborators().add("Vladimir");
        MatcherAssert.assertThat(
            repo.assignees().check("Vladimir"),
            Matchers.is(true)
        );
    }

    /**
     * MkAssignees can check if the owner is an assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    public void checkOwnerIsAssigneeForRepo() throws Exception {
        MatcherAssert.assertThat(
            MkAssigneesTest.repo().assignees().check("Jonathan"),
            Matchers.is(true)
        );
    }

    /**
     * Create a repo to work with.
     * @return Repo
     */
    private static Repo repo() throws IOException {
        return new MkGitHub("Jonathan").randomRepo();
    }
}
