/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Collaborators;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkCollaborators}.
 * @since 0.7
 */
final class MkCollaboratorsTest {

    /**
     * MkCollaborators can add, remove and iterate collaborators.
     * @throws Exception If some problem inside
     */
    @Test
    void addAndRemove() throws Exception {
        final Collaborators collabs = MkCollaboratorsTest.collaborators();
        final String login = "some_user";
        collabs.add(login);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            collabs.iterate(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            collabs.iterate().iterator().next().login(),
            Matchers.equalTo(login)
        );
        collabs.remove(login);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            collabs.iterate(),
            Matchers.iterableWithSize(0)
        );
    }

    /**
     * MkCollaborators can check whether  user is collaborator or not.
     * @throws Exception If some problem inside
     */
    @Test
    void isCollaborator() throws Exception {
        final Collaborators collabs = MkCollaboratorsTest.collaborators();
        final String collaborator = "collaborator";
        collabs.add(collaborator);
        MatcherAssert.assertThat(
            "Values are not equal",
            collabs.isCollaborator(collaborator),
            Matchers.equalTo(true)
        );
        final String stranger = "stranger";
        MatcherAssert.assertThat(
            "Values are not equal",
            collabs.isCollaborator(stranger),
            Matchers.equalTo(false)
        );
    }

    /**
     * Create a collaborators to work with.
     * @return Collaborators just created
     */
    private static Collaborators collaborators() throws IOException {
        return new MkGitHub().randomRepo().collaborators();
    }
}
