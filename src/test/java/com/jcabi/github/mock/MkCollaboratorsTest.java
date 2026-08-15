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
     * Login of the collaborator.
     */
    private static final String LOGIN = "some_user";

    /**
     * MkCollaborators can add a collaborator.
     * @throws Exception If some problem inside
     */
    @Test
    void adds() throws Exception {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkCollaboratorsTest.added().iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkCollaborators can remember the login of a collaborator.
     * @throws Exception If some problem inside
     */
    @Test
    void remembersLogin() throws Exception {
        MatcherAssert.assertThat(
            "Collaborator has a wrong login",
            MkCollaboratorsTest.added().iterate().iterator().next().login(),
            Matchers.equalTo(MkCollaboratorsTest.LOGIN)
        );
    }

    /**
     * MkCollaborators can remove a collaborator.
     * @throws Exception If some problem inside
     */
    @Test
    void removes() throws Exception {
        final Collaborators collabs = MkCollaboratorsTest.added();
        collabs.remove(MkCollaboratorsTest.LOGIN);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            collabs.iterate(),
            Matchers.iterableWithSize(0)
        );
    }

    /**
     * MkCollaborators can recognize a collaborator.
     * @throws Exception If some problem inside
     */
    @Test
    void recognizesCollaborator() throws Exception {
        MatcherAssert.assertThat(
            "Collaborator is not recognized",
            MkCollaboratorsTest.added()
                .isCollaborator(MkCollaboratorsTest.LOGIN),
            Matchers.equalTo(true)
        );
    }

    /**
     * MkCollaborators can recognize a stranger.
     * @throws Exception If some problem inside
     */
    @Test
    void recognizesStranger() throws Exception {
        MatcherAssert.assertThat(
            "Stranger is taken for a collaborator",
            MkCollaboratorsTest.added().isCollaborator("stranger"),
            Matchers.equalTo(false)
        );
    }

    /**
     * Collaborators with one collaborator in them.
     * @return Collaborators just created
     * @throws IOException If some problem inside
     */
    private static Collaborators added() throws IOException {
        final Collaborators collabs = new MkGitHub().randomRepo()
            .collaborators();
        collabs.add(MkCollaboratorsTest.LOGIN);
        return collabs;
    }
}
