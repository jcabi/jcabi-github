/**
 * Copyright (c) 2013-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.User;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkAssignees}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
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
            repo().assignees().iterate(),
            Matchers.not(Matchers.emptyIterableOf(User.class))
        );
    }

    /**
     * MkAssignees can check if a collaborator is an assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    public void checkCollaboratorIsAssigneeForRepo() throws Exception {
        final Repo repo = repo();
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
            repo().assignees().check("Jonathan"),
            Matchers.is(true)
        );
    }

    /**
     * Create a repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        return new MkGithub("Jonathan").repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }
}
