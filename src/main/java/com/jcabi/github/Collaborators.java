/**
 * Copyright (c) 2013-2024, jcabi.com
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

package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Github repository collaborators.
 * @author Aleksey Popov (alopen@yandex.ru)
 * @version $Id$
 * @since 0.8
 */
@Immutable
public interface Collaborators {
    /**
     * Permission levels a user can be granted in an organization repository.
     *
     * @see <a href="https://developer.github.com/v3/repos/collaborators/#parameters-1">Add user with permissions</a>
     */
    enum Permission { PULL, PUSH, ADMIN, MAINTAIN, TRIAGE };

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Check if a user is collaborator.
     *
     * @param user User
     * @return True is a user is a collaborator, otherwise returns false
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/collaborators/#get">
     *  Check if a user is collaborator</a>
     */
    boolean isCollaborator(String user) throws IOException;

    /**
     * Add user as a collaborator.
     *
     * @param user User
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/collaborators/#add-collaborator">Add user as a collaborator</a>
     */
    void add(String user) throws IOException;

    /**
     * Add user with permissions. Only works on an organization repository
     *
     * @param user User to add
     * @param permission Permission level to grant
     * @throws IOException if there is an I/O problem
     * @see <a href=https://developer.github.com/v3/repos/collaborators/#add-user-as-a-collaborator">Add user as a collaborator</a>
     */
    void addWithPermission(String user,
        Collaborators.Permission permission) throws IOException;

    /**
     * Get user permission in this repo.
     *
     * @param user User to check
     * @return Permission level granted, incl. "admin", "write",
     *  "read", or "none"
     * @throws IOException if there is an I/O problem
     * @see <a href=https://docs.github.com/en/rest/collaborators/collaborators#get-repository-permissions-for-a-user">Get repository permissions for a user</a>
     */
    String permission(String user) throws IOException;

    /**
     * Remove user as a collaborator.
     *
     * @param user User
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/collaborators/#remove-collaborator">Remove user as a collaborator</a>
     */
    void remove(String user) throws IOException;

    /**
     * Iterates over repo collaborators.
     * @return Iterator on repo collaborators.
     */
    Iterable<User> iterate();
}
