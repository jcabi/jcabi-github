/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.User;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkAssignees}.
 * @since 0.7
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
final class MkAssigneesTest {

    /**
     * MkAssignees can iterate over assignees.
     * @throws Exception Exception If some problem inside
     */
    @Test
    void iteratesAssignees() throws Exception {
        MatcherAssert.assertThat(
            "Collection is not empty",
            MkAssigneesTest.repo().assignees().iterate(),
            Matchers.not(Matchers.emptyIterableOf(User.class))
        );
    }

    /**
     * MkAssignees can check if a collaborator is an assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    void checkCollaboratorIsAssigneeForRepo() throws Exception {
        final Repo repo = MkAssigneesTest.repo();
        repo.collaborators().add("Vladimir");
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.assignees().check("Vladimir"),
            Matchers.is(true)
        );
    }

    /**
     * MkAssignees can check if the owner is an assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    void checkOwnerIsAssigneeForRepo() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
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
