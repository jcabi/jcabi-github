/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Collaborators;
import com.jcabi.github.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkCollaborators}.
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
            collaborators.iterate(),
            Matchers.<User>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            collaborators.iterate().iterator().next().login(),
            Matchers.equalTo(login)
        );
        collaborators.remove(login);
        MatcherAssert.assertThat(
            collaborators.iterate(),
            Matchers.<User>iterableWithSize(0)
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
        final String stranger = "stranger";
        collaborators.add(collaborator);
        MatcherAssert.assertThat(
            collaborators.isCollaborator(collaborator),
            Matchers.equalTo(true)
        );
        MatcherAssert.assertThat(
            collaborators.isCollaborator(stranger),
            Matchers.equalTo(false)
        );
    }

    /**
     * Create a collaborators to work with.
     * @return Collaborators just created
     * @throws Exception If some problem inside
     */
    private Collaborators collaborators() throws Exception {
        return new MkGithub().randomRepo().collaborators();
    }
}
