/**
 * Copyright (c) 2013-2025, jcabi.com
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
 * Public members of a GitHub organization.
 *
 * @see <a href="https://developer.github.com/v3/orgs/members/">Organization Members API</a>
 * @since 0.24
 */
@Immutable
public interface PublicMembers {
    /**
     * Organization of which these are public members.
     * @return Organization
     */
    Organization org();

    /**
     * Conceal a user's membership from public view.
     * @param user User whose membership to conceal
     * @throws IOException If an I/O problem occurs
     * @see <a href="https://developer.github.com/v3/orgs/members/#conceal-a-users-membership">Conceal a user's membership</a>
     */
    void conceal(
        User user
    ) throws IOException;

    /**
     * Make a user's membership publicly visible.
     * @param user User whose membership to publicize
     * @throws IOException If an I/O problem occurs
     * @see <a href="https://developer.github.com/v3/orgs/members/#publicize-a-users-membership">Publicize a user's membership</a>
     */
    void publicize(
        User user
    ) throws IOException;

    /**
     * Get all users who are public members of this organization.
     * @return Members
     * @see <a href="https://developer.github.com/v3/orgs/members/#public-members-list">Public members list</a>
     */
    Iterable<User> iterate();

    /**
     * Check whether the user is a public member of this organization.
     * @param user User to check public organization membership of
     * @return Is the user a public member of this organization?
     * @throws IOException If an I/O problem occurs
     * @see <a href="https://developer.github.com/v3/orgs/members/#check-public-membership">Check public membership</a>
     */
    boolean contains(
        User user
    ) throws IOException;
}
