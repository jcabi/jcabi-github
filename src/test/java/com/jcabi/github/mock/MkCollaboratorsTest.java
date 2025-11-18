/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Collaborators;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkCollaborators}.
 * @since 0.7
 */
public final class MkCollaboratorsTest {

    /**
     * MkCollaborators can add, remove and iterate collaborators.
     * @throws Exception If some problem inside
     */
    @Test
    public void addAndRemove() throws Exception {
        final Collaborators collaborators = this.collaborators();
        final String login = "some_user";
        collaborators.add(login);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            collaborators.iterate(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            collaborators.iterate().iterator().next().login(),
            Matchers.equalTo(login)
        );
        collaborators.remove(login);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            collaborators.iterate(),
            Matchers.iterableWithSize(0)
        );
    }

    /**
     * MkCollaborators can check whether  user is collaborator or not.
     * @throws Exception If some problem inside
     */
    @Test
    public void isCollaborator() throws Exception {
        final Collaborators collaborators = this.collaborators();
        final String collaborator = "collaborator";
        collaborators.add(collaborator);
        MatcherAssert.assertThat(
            "Values are not equal",
            collaborators.isCollaborator(collaborator),
            Matchers.equalTo(true)
        );
        final String stranger = "stranger";
        MatcherAssert.assertThat(
            "Values are not equal",
            collaborators.isCollaborator(stranger),
            Matchers.equalTo(false)
        );
    }

    /**
     * Create a collaborators to work with.
     * @return Collaborators just created
     */
    private Collaborators collaborators() throws IOException {
        return new MkGitHub().randomRepo().collaborators();
    }
}
